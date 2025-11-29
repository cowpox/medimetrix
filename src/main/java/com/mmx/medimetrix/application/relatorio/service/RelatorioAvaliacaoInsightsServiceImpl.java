package com.mmx.medimetrix.application.relatorio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmx.medimetrix.application.relatorio.vm.*;
import com.mmx.medimetrix.infrastructure.gemini.GeminiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de Insights por médico.
 *
 * Estratégia:
 *  - sempre calcula um texto-base heurístico (sem IA),
 *  - se houver chave configurada do Gemini, chama a LLM com os dados numéricos,
 *    pedindo um JSON com os 4 blocos de texto,
 *  - se a chamada falhar, retorna o heurístico.
 */
@Service
public class RelatorioAvaliacaoInsightsServiceImpl implements RelatorioAvaliacaoInsightsService {

    private static final Logger log = LoggerFactory.getLogger(RelatorioAvaliacaoInsightsServiceImpl.class);

    private final RelatorioAvaliacaoService relatorioAvaliacaoService;
    private final GeminiProperties properties;
    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public RelatorioAvaliacaoInsightsServiceImpl(RelatorioAvaliacaoService relatorioAvaliacaoService,
                                                 GeminiProperties properties,
                                                 WebClient geminiWebClient) {
        this.relatorioAvaliacaoService = relatorioAvaliacaoService;
        this.properties = properties;
        this.webClient = geminiWebClient;
    }

    // ==========================================================
    // API pública
    // ==========================================================

    @Override
    public RelatorioMedicoInsightsVM gerarInsightsParaMedico(Long idAvaliacao, Long idMedico) {

        RelatorioAvaliacaoMedicoVM relatorio =
                relatorioAvaliacaoService.detalharAvaliacaoPorMedico(idAvaliacao, idMedico);

        AvaliacaoMedicoResumoVM resumo = relatorio.resumo();
        List<CriterioRadarVM> criterios = relatorio.criterios();

        // 1) Calcula um texto base puramente heurístico (sem IA)
        RelatorioMedicoInsightsVM heuristico =
                gerarInsightsHeuristico(resumo, criterios);

        // 2) Se não há chave configurada, paramos aqui
        if (properties == null || !StringUtils.hasText(properties.getKey())) {
            log.warn("Gemini API key não configurada. Retornando apenas insights heurísticos.");
            return heuristico;
        }

        try {
            // 3) Monta o prompt para a LLM
            String prompt = buildPromptParaLlm(resumo, criterios, heuristico);

            // 4) Chama o Gemini
            String respostaJson = chamarGemini(prompt);

            // 5) Interpreta a resposta do Gemini
            return parseGeminiResponse(respostaJson, resumo, criterios);

        } catch (Exception e) {
            log.error("Falha ao gerar insights com Gemini. Usando versão heurística.", e);
            return heuristico;
        }
    }

    // ==========================================================
    // 1. Geração heurística (sem IA)
    // ==========================================================

    private RelatorioMedicoInsightsVM gerarInsightsHeuristico(AvaliacaoMedicoResumoVM resumo,
                                                              List<CriterioRadarVM> criterios) {

        BigDecimal mediaMedico = safe(resumo.media());
        BigDecimal mediaGrupo = safe(resumo.mediaGrupo());
        BigDecimal menorGrupo = safe(resumo.menorNotaGrupo());
        BigDecimal maiorGrupo = safe(resumo.maiorNotaGrupo());

        String tituloAvaliacao = optional(resumo.tituloAvaliacao());
        String unidade = optional(resumo.nomeUnidade());
        String especialidade = optional(resumo.nomeEspecialidade());

        // Diferença entre médico e grupo
        BigDecimal diffGlobal = mediaMedico.subtract(mediaGrupo);

        // Top positivos / negativos por critério
        List<CriterioRadarVM> criteriosComAmbos = criterios.stream()
                .filter(c -> c.notaMedico() != null && c.notaGrupo() != null)
                .collect(Collectors.toList());

        List<CriterioRadarVM> topPositivos = criteriosComAmbos.stream()
                .sorted((a, b) -> diff(b).compareTo(diff(a)))
                .limit(3)
                .collect(Collectors.toList());

        List<CriterioRadarVM> topNegativos = criteriosComAmbos.stream()
                .sorted(Comparator.comparing(RelatorioAvaliacaoInsightsServiceImpl::diff))
                .limit(3)
                .collect(Collectors.toList());

        // ----- Visão geral ---------------------------------------------------
        StringBuilder visao = new StringBuilder();
        visao.append("Nesta avaliação (")
                .append(tituloAvaliacao)
                .append("), conduzida no contexto da unidade ")
                .append(unidade.isBlank() ? "assistencial avaliada" : unidade)
                .append(", o profissional apresentou desempenho global de ")
                .append(format(mediaMedico))
                .append(" em uma escala de 0 a 5, enquanto a média do grupo foi de ")
                .append(format(mediaGrupo))
                .append(". ");

        if (maiorGrupo.compareTo(BigDecimal.ZERO) > 0) {
            visao.append("As notas do grupo variaram de ")
                    .append(format(menorGrupo))
                    .append(" a ")
                    .append(format(maiorGrupo))
                    .append(", posicionando o profissional ");
        }

        if (diffGlobal.compareTo(BigDecimal.valueOf(0.15)) > 0) {
            visao.append("acima da média geral dos pares, ");
        } else if (diffGlobal.compareTo(BigDecimal.valueOf(-0.15)) < 0) {
            visao.append("ligeiramente abaixo da média dos pares, ");
        } else {
            visao.append("muito próximo à média dos pares, ");
        }

        visao.append("no cenário de atuação em ")
                .append(especialidade.isBlank() ? "sua especialidade assistencial" : especialidade)
                .append(".");

        // ----- Destaques positivos ------------------------------------------
        StringBuilder destaques = new StringBuilder();
        destaques.append("Os principais pontos fortes, quando comparados à média do grupo, ")
                .append("incluem: ");

        if (topPositivos.isEmpty()) {
            destaques.append("não foram observados critérios com diferença relevante positiva, ")
                    .append("o que indica desempenho homogêneo em relação aos pares.");
        } else {
            List<String> descricoes = new ArrayList<>();
            for (CriterioRadarVM c : topPositivos) {
                BigDecimal diff = diff(c);
                descricoes.add(c.nomeCriterio() +
                        " (profissional " + format(c.notaMedico()) +
                        " vs. grupo " + format(c.notaGrupo()) +
                        ", diferença de " + format(diff) + ")");
            }
            destaques.append(String.join("; ", descricoes)).append(".");
            destaques.append(" Esses resultados sugerem que o profissional pode atuar como referência ")
                    .append("nesses temas, compartilhando boas práticas com o time.");
        }

        // ----- Pontos de atenção --------------------------------------------
        StringBuilder atencao = new StringBuilder();
        atencao.append("Foram identificadas oportunidades de desenvolvimento nos critérios em que ")
                .append("a nota do profissional ficou abaixo da média do grupo. ");

        List<CriterioRadarVM> negativosRelevantes = topNegativos.stream()
                .filter(c -> diff(c).compareTo(BigDecimal.valueOf(-0.20)) < 0)
                .collect(Collectors.toList());

        if (negativosRelevantes.isEmpty()) {
            atencao.append("As diferenças observadas são discretas e não configuram fragilidades importantes, ")
                    .append("mas podem orientar ajustes finos na prática cotidiana.");
        } else {
            List<String> descricoes = new ArrayList<>();
            for (CriterioRadarVM c : negativosRelevantes) {
                BigDecimal diff = diff(c);
                descricoes.add(c.nomeCriterio() +
                        " (profissional " + format(c.notaMedico()) +
                        " vs. grupo " + format(c.notaGrupo()) +
                        ", diferença de " + format(diff) + ")");
            }
            atencao.append("Ganham destaque: ")
                    .append(String.join("; ", descricoes))
                    .append(". Esses critérios merecem atenção específica nos próximos ciclos.");
        }

        // ----- Sugestões de desenvolvimento ---------------------------------
        StringBuilder sugestoes = new StringBuilder();
        sugestoes.append("Como próximos passos, recomenda-se que o profissional, em alinhamento com a liderança da unidade, ")
                .append("estruture um plano de desenvolvimento focado nos critérios com maior diferença negativa em relação ao grupo. ")
                .append("Podem ser úteis ações como: participação em atividades de educação continuada direcionadas, ")
                .append("revisão dos fluxos de trabalho na unidade, uso mais sistemático das ferramentas de registro e informação clínica ")
                .append("e troca estruturada de experiências com colegas que apresentam resultados consistentes nesses domínios. ")
                .append("Ao mesmo tempo, é importante reconhecer e preservar os pontos fortes já demonstrados, ")
                .append("especialmente naqueles critérios em que o desempenho superou o padrão do grupo.");

        return new RelatorioMedicoInsightsVM(
                resumo,
                criterios,
                visao.toString(),
                destaques.toString(),
                atencao.toString(),
                sugestoes.toString()
        );
    }

    // ==========================================================
    // 2. Construção do prompt para a LLM
    // ==========================================================

    private String buildPromptParaLlm(AvaliacaoMedicoResumoVM resumo,
                                      List<CriterioRadarVM> criterios,
                                      RelatorioMedicoInsightsVM heuristico) {

        StringBuilder sb = new StringBuilder();

        sb.append("Você é uma IA especializada em análise de desempenho médico em cooperativas de saúde.\n")
                .append("Sua tarefa é analisar os dados numéricos a seguir e gerar um texto em português, ")
                .append("voltado a gestores e médicos, com linguagem respeitosa, ética e construtiva.\n\n")

                .append("NUNCA use o nome do médico. Use termos como \"o profissional avaliado\" ou \"o médico\".\n")
                .append("Não invente informações que não estejam nos dados. Baseie-se apenas nos números fornecidos.\n\n")

                .append("Contexto da avaliação:\n")
                .append("- Título da avaliação: ").append(optional(resumo.tituloAvaliacao())).append("\n")
                .append("- Unidade de atuação: ").append(optional(resumo.nomeUnidade())).append("\n")
                .append("- Especialidade: ").append(optional(resumo.nomeEspecialidade())).append("\n")
                .append("- Nota global do profissional (0-5): ").append(format(safe(resumo.media()))).append("\n")
                .append("- Nota média do grupo (0-5): ").append(format(safe(resumo.mediaGrupo()))).append("\n")
                .append("- Menor nota global do grupo: ").append(format(safe(resumo.menorNotaGrupo()))).append("\n")
                .append("- Maior nota global do grupo: ").append(format(safe(resumo.maiorNotaGrupo()))).append("\n\n")

                .append("Notas por critério (todas em escala 0 a 5):\n");

        for (CriterioRadarVM c : criterios) {
            sb.append("- Critério: ").append(c.nomeCriterio())
                    .append(" | Nota do profissional: ").append(format(safe(c.notaMedico())))
                    .append(" | Nota média do grupo: ").append(format(safe(c.notaGrupo())))
                    .append("\n");
        }

        sb.append("\nCom base APENAS nesses dados, produza um JSON com a seguinte estrutura exata:\n")
                .append("{\n")
                .append("  \"visao_geral\": \"...\",\n")
                .append("  \"destaques_positivos\": \"...\",\n")
                .append("  \"pontos_atencao\": \"...\",\n")
                .append("  \"sugestoes_desenvolvimento\": \"...\"\n")
                .append("}\n\n")
                .append("Onde:\n")
                .append("- \"visao_geral\" faz um panorama da avaliação, comparando o profissional com o grupo.\n")
                .append("- \"destaques_positivos\" descreve os principais pontos fortes.\n")
                .append("- \"pontos_atencao\" aponta, de forma cuidadosa, onde há espaço para evolução.\n")
                .append("- \"sugestoes_desenvolvimento\" traz recomendações práticas de desenvolvimento.\n")
                .append("Use tom profissional, respeitoso, sem juízos pessoais ou termos pejorativos.\n");

        // Como fallback semântico, passamos também o texto heurístico que já geramos
        sb.append("\nA seguir, um rascunho heurístico já gerado pelo sistema, que você pode usar como referência, ")
                .append("melhorando coesão e clareza, mas mantendo o conteúdo coerente com os dados numéricos:\n\n")
                .append("VISÃO GERAL (rascunho):\n").append(heuristico.visaoGeral()).append("\n\n")
                .append("DESTAQUES POSITIVOS (rascunho):\n").append(heuristico.destaquesPositivos()).append("\n\n")
                .append("PONTOS DE ATENÇÃO (rascunho):\n").append(heuristico.pontosAtencao()).append("\n\n")
                .append("SUGESTÕES (rascunho):\n").append(heuristico.sugestoesDesenvolvimento()).append("\n\n");

        return sb.toString();
    }

    // ==========================================================
    // 3. Chamada ao Gemini
    // ==========================================================

    private String chamarGemini(String prompt) {

        try {
            String model = properties.getModel(); // ex.: "gemini-flash-latest"

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    )
            );

            String json = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/{model}:generateContent")
                            .queryParam("key", properties.getKey())
                            .build(model)
                    )
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Resposta bruta do Gemini: {}", json);

            JsonNode root = mapper.readTree(json);
            JsonNode textNode = root
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            return textNode.asText();

        } catch (Exception e) {
            log.error("Erro ao chamar Gemini.", e);
            throw new RuntimeException("Erro ao chamar Gemini.", e);
        }
    }

    // ==========================================================
    // 4. Parser da resposta do Gemini
    // ==========================================================

    private RelatorioMedicoInsightsVM parseGeminiResponse(
            String json,
            AvaliacaoMedicoResumoVM resumo,
            List<CriterioRadarVM> criterios
    ) {
        try {
            // 1) Remove ```json ... ``` se vier em formato de bloco de código
            String cleaned = limparMarkdownJson(json);

            JsonNode root = mapper.readTree(cleaned);

            String visaoGeral = root.path("visao_geral").asText(null);
            String destaques = root.path("destaques_positivos").asText(null);
            String atencao   = root.path("pontos_atencao").asText(null);
            String sugestoes = root.path("sugestoes_desenvolvimento").asText(null);

            return new RelatorioMedicoInsightsVM(
                    resumo,
                    criterios,
                    visaoGeral,
                    destaques,
                    atencao,
                    sugestoes
            );
        } catch (Exception e) {
            log.error("Falha ao interpretar resposta do Gemini. Usando heurístico.", e);
            // fallback heurístico
            return gerarInsightsHeuristico(resumo, criterios);
        }
    }

    /**
     * Se o modelo devolver o JSON dentro de ``` ou ```json,
     * remove essas marcas e retorna apenas o JSON puro.
     */
    private String limparMarkdownJson(String text) {
        if (text == null) return null;

        String trimmed = text.trim();

        // Começa com ``` (```json, ```JSON, etc)
        if (trimmed.startsWith("```")) {
            int firstNewLine = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");

            if (firstNewLine != -1 && lastFence != -1 && lastFence > firstNewLine) {
                // Pega só o conteúdo entre a primeira quebra de linha e o último ```
                String inner = trimmed.substring(firstNewLine + 1, lastFence);
                return inner.trim();
            }
        }

        return trimmed;
    }

    // ==========================================================
    // Helpers numéricos / strings
    // ==========================================================

    private static BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private static String optional(String s) {
        return s != null ? s : "";
    }

    private static String format(BigDecimal value) {
        return value == null
                ? "-"
                : value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private static BigDecimal diff(CriterioRadarVM c) {
        if (c == null || c.notaMedico() == null || c.notaGrupo() == null) {
            return BigDecimal.ZERO;
        }
        return c.notaMedico().subtract(c.notaGrupo());
    }
}

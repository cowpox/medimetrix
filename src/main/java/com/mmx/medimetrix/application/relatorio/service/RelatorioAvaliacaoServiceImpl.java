package com.mmx.medimetrix.application.relatorio.service;

import com.mmx.medimetrix.application.avaliacao.service.AvaliacaoService;
import com.mmx.medimetrix.application.criterio.service.CriterioService;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.application.medico.service.MedicoService;
import com.mmx.medimetrix.application.participacao.service.ParticipacaoService;
import com.mmx.medimetrix.application.questao.service.QuestaoService;
import com.mmx.medimetrix.application.relatorio.vm.*;
import com.mmx.medimetrix.application.resposta.service.RespostaService;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.domain.core.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class RelatorioAvaliacaoServiceImpl implements RelatorioAvaliacaoService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int BIG_SIZE = 1000;
    private static final Logger log = LoggerFactory.getLogger(RelatorioAvaliacaoServiceImpl.class);


    private final AvaliacaoService avaliacaoService;
    private final ParticipacaoService participacaoService;
    private final RespostaService respostaService;
    private final UsuarioService usuarioService;
    private final QuestaoService questaoService;
    private final MedicoService medicoService;
    private final UnidadeService unidadeService;
    private final EspecialidadeService especialidadeService;
    private final CriterioService criterioService;

    public RelatorioAvaliacaoServiceImpl(AvaliacaoService avaliacaoService,
                                         ParticipacaoService participacaoService,
                                         RespostaService respostaService,
                                         UsuarioService usuarioService,
                                         QuestaoService questaoService,
                                         MedicoService medicoService,
                                         UnidadeService unidadeService,
                                         EspecialidadeService especialidadeService,
                                         CriterioService criterioService) {
        this.avaliacaoService = avaliacaoService;
        this.participacaoService = participacaoService;
        this.respostaService = respostaService;
        this.usuarioService = usuarioService;
        this.questaoService = questaoService;
        this.medicoService = medicoService;
        this.unidadeService = unidadeService;
        this.especialidadeService = especialidadeService;
        this.criterioService = criterioService;
    }

    // ==========================================================
    // Lista de avaliações para a tela de relatórios
    // ==========================================================
    @Override
    public List<AvaliacaoResumoVM> listarAvaliacoes(String termo, String status) {
        List<Avaliacao> todas = avaliacaoService.listPaged(0, BIG_SIZE);

        // filtra: não mostra rascunho
        List<Avaliacao> filtradas = todas.stream()
                .filter(a -> !"RASCUNHO".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        // filtro por status
        if (status != null && !status.isBlank()) {
            String s = status.toUpperCase();
            filtradas = filtradas.stream()
                    .filter(a -> s.equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());
        }

        // filtro por termo no título
        if (termo != null && !termo.isBlank()) {
            String t = termo.toLowerCase();
            filtradas = filtradas.stream()
                    .filter(a -> a.getTitulo() != null && a.getTitulo().toLowerCase().contains(t))
                    .collect(Collectors.toList());
        }

        List<AvaliacaoResumoVM> avaliacoes = new ArrayList<>();
        for (Avaliacao a : filtradas) {
            List<Participacao> parts = participacaoService.listByAvaliacao(a.getIdAvaliacao(), 0, BIG_SIZE);

            long total = parts.size();
            long respondidas = parts.stream().filter(p -> "RESPONDIDA".equals(p.getStatus())).count();
            long emAndamento = parts.stream().filter(p -> "EM_ANDAMENTO".equals(p.getStatus())).count();
            long pendentes = parts.stream().filter(p -> "PENDENTE".equals(p.getStatus())).count();

            BigDecimal adesao = BigDecimal.ZERO;
            if (total > 0) {
                adesao = BigDecimal
                        .valueOf((respondidas * 100.0) / total)
                        .setScale(1, RoundingMode.HALF_UP);
            }

            String periodo = "-";
            if (a.getDataInicioAplic() != null && a.getDataFimAplic() != null) {
                periodo = a.getDataInicioAplic().format(DF)
                        + " a "
                        + a.getDataFimAplic().format(DF);
            }

            avaliacoes.add(new AvaliacaoResumoVM(
                    a.getIdAvaliacao(),
                    a.getTitulo(),
                    periodo,
                    a.getStatus(),
                    total,
                    respondidas,
                    emAndamento,
                    pendentes,
                    adesao
            ));
        }

        avaliacoes.sort(Comparator.comparing(AvaliacaoResumoVM::titulo));
        return avaliacoes;
    }

    // ==========================================================
    // Relatório consolidado da avaliação
    // ==========================================================
    @Override
    public RelatorioAvaliacaoDetalheVM detalharAvaliacao(Long idAvaliacao) {
        return this.detalharAvaliacaoOriginal(idAvaliacao);
    }

    private RelatorioAvaliacaoDetalheVM detalharAvaliacaoOriginal(Long idAvaliacao) {
        Avaliacao avaliacao = avaliacaoService.findById(idAvaliacao)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        List<Participacao> participacoes =
                participacaoService.listByAvaliacao(avaliacao.getIdAvaliacao(), 0, BIG_SIZE);

        long total = participacoes.size();
        long respondidas = participacoes.stream().filter(p -> "RESPONDIDA".equals(p.getStatus())).count();
        long emAndamento = participacoes.stream().filter(p -> "EM_ANDAMENTO".equals(p.getStatus())).count();
        long pendentes = participacoes.stream().filter(p -> "PENDENTE".equals(p.getStatus())).count();

        BigDecimal adesao = BigDecimal.ZERO;
        if (total > 0) {
            adesao = BigDecimal
                    .valueOf((respondidas * 100.0) / total)
                    .setScale(1, RoundingMode.HALF_UP);
        }

        String periodo = "-";
        if (avaliacao.getDataInicioAplic() != null && avaliacao.getDataFimAplic() != null) {
            periodo = avaliacao.getDataInicioAplic().format(DF)
                    + " a "
                    + avaliacao.getDataFimAplic().format(DF);
        }

        AvaliacaoResumoVM resumo = new AvaliacaoResumoVM(
                avaliacao.getIdAvaliacao(),
                avaliacao.getTitulo(),
                periodo,
                avaliacao.getStatus(),
                total,
                respondidas,
                emAndamento,
                pendentes,
                adesao
        );

        int totalQuestoesNumericas = contarQuestoesNumericas(participacoes);

        List<ParticipacaoResumoVM> medicos = new ArrayList<>();
        for (Participacao p : participacoes) {
            Long idMedico = p.getAvaliadoMedicoId();
            String nomeMedico;
            try {
                nomeMedico = usuarioService.getById(idMedico).getNome();
            } catch (Exception e) {
                nomeMedico = "(médico não encontrado)";
            }
            BigDecimal media = calcularNotaGlobalParticipacao(p, totalQuestoesNumericas);
            medicos.add(new ParticipacaoResumoVM(
                    p.getIdParticipacao(),
                    idMedico,
                    nomeMedico,
                    p.getStatus(),
                    media
            ));
        }

        medicos.sort(Comparator.comparing(ParticipacaoResumoVM::nomeMedico));
        return new RelatorioAvaliacaoDetalheVM(resumo, medicos);
    }

    // ==========================================================
    // Relatório por médico (com radar por critério)
    // ==========================================================
    @Override
    public RelatorioAvaliacaoMedicoVM detalharAvaliacaoPorMedico(Long idAvaliacao, Long idMedico) {

        Avaliacao avaliacao = avaliacaoService.findById(idAvaliacao)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        List<Participacao> participacoes =
                participacaoService.listByAvaliacao(avaliacao.getIdAvaliacao(), 0, BIG_SIZE);

        // Quantos realmente responderam
        long respondidas = participacoes.stream()
                .filter(p -> "RESPONDIDA".equalsIgnoreCase(p.getStatus()))
                .count();

// k-mínimo configurado na avaliação
        Integer kConfig = avaliacao.getkMinimo();
        int kMin = (kConfig != null ? kConfig : 0);

// Regra de anonimato: só exibe grupo se atingiu k-mínimo E tiver mais de 1 respondente
        boolean exibirComparativoGrupo = respondidas >= kMin && respondidas > 1;




        Participacao participacao = participacoes.stream()
                .filter(p -> Objects.equals(p.getAvaliadoMedicoId(), idMedico))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Participação do médico não encontrada"));

        String nomeMedico;
        try {
            nomeMedico = usuarioService.getById(idMedico).getNome();
        } catch (Exception e) {
            nomeMedico = "(médico não encontrado)";
        }

        Medico medico = medicoService.findByUsuarioId(idMedico).orElse(null);
        String nomeEspecialidade = "";
        String nomeUnidade = "";

        if (medico != null) {
            Long espId = medico.getEspecialidadeId();
            Long unidId = medico.getUnidadeId();
            if (espId != null) {
                Optional<Especialidade> esp = especialidadeService.findById(espId);
                nomeEspecialidade = esp.map(Especialidade::getNome).orElse("");
            }
            if (unidId != null) {
                Optional<Unidade> unid = unidadeService.findById(unidId);
                nomeUnidade = unid.map(Unidade::getNome).orElse("");
            }
        }

        // 1. Total de questões numéricas da avaliação inteira (denominador global)
        int totalQuestoesNumericas = contarQuestoesNumericas(participacoes);

        // 2. Mapa de notas normalizadas (0–5) por questão para o grupo
        Map<Long, List<BigDecimal>> notasGrupoPorQuestao = new HashMap<>();

        if (exibirComparativoGrupo) {
            for (Participacao p : participacoes) {
                List<Resposta> respostasP = respostaService.listByParticipacao(p.getIdParticipacao());
                for (Resposta r : respostasP) {
                    BigDecimal valor = r.getValorNumerico();
                    if (valor != null && r.getIdQuestao() != null) {
                        Questao q = questaoService.findById(r.getIdQuestao()).orElse(null);
                        BigDecimal maxEscala = obterEscalaMax(q);
                        if (maxEscala != null && maxEscala.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal normalizada = normalizarPara05(valor, maxEscala);
                            notasGrupoPorQuestao
                                    .computeIfAbsent(r.getIdQuestao(), k -> new ArrayList<>())
                                    .add(normalizada);
                        }
                    }
                }
            }
        }


        // 2.1 Agrega por critério – grupo
        Map<Long, Long> questaoParaCriterio = new HashMap<>();
        Map<Long, BigDecimal> somaGrupoPorCriterio = new HashMap<>();
        Map<Long, Integer> qtdeGrupoPorCriterio = new HashMap<>();

        for (Map.Entry<Long, List<BigDecimal>> entry : notasGrupoPorQuestao.entrySet()) {
            Long idQuestao = entry.getKey();
            List<BigDecimal> notasNormalizadasQuestao = entry.getValue();

            Questao q = questaoService.findById(idQuestao).orElse(null);
            Long idCriterio = extrairIdCriterio(q);
            if (idCriterio == null) continue;

            questaoParaCriterio.put(idQuestao, idCriterio);

            BigDecimal somaQ = BigDecimal.ZERO;
            int countQ = 0;
            for (BigDecimal n : notasNormalizadasQuestao) {
                if (n == null) continue;
                somaQ = somaQ.add(n);
                countQ++;
            }
            if (countQ == 0) continue;

            somaGrupoPorCriterio.merge(idCriterio, somaQ, BigDecimal::add);
            qtdeGrupoPorCriterio.merge(idCriterio, countQ, Integer::sum);
        }

        // 3. Estatísticas globais da avaliação (mín/máx/média do grupo)
        List<BigDecimal> notasGlobaisDosColegas = new ArrayList<>();
        for (Participacao p : participacoes) {
            BigDecimal notaGlobalParticipante = calcularNotaGlobalParticipacao(p, totalQuestoesNumericas);
            if (notaGlobalParticipante != null) {
                notasGlobaisDosColegas.add(notaGlobalParticipante);
            }
        }

        BigDecimal mediaGrupo = null;
        BigDecimal menorNotaGp = null;
        BigDecimal maiorNotaGp = null;

        if (exibirComparativoGrupo && !notasGlobaisDosColegas.isEmpty()) {
            menorNotaGp = notasGlobaisDosColegas.stream()
                    .min(Comparator.naturalOrder())
                    .orElse(null);

            maiorNotaGp = notasGlobaisDosColegas.stream()
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            BigDecimal soma = notasGlobaisDosColegas.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            mediaGrupo = soma.divide(
                    BigDecimal.valueOf(notasGlobaisDosColegas.size()),
                    2,
                    RoundingMode.HALF_UP
            );
        }


        // 4. Respostas do médico
        List<Resposta> respostasMedico = respostaService.listByParticipacao(participacao.getIdParticipacao());
        List<BigDecimal> notasOriginais = new ArrayList<>();
        List<BigDecimal> notasNormalizadasMedico = new ArrayList<>();
        List<QuestaoNotaVM> questoes = new ArrayList<>();
        int ordem = 1;

        // mapas para agregação por critério (médico)
        Map<Long, BigDecimal> somaMedicoPorCriterio = new HashMap<>();
        Map<Long, Integer> qtdeMedicoPorCriterio = new HashMap<>();

        respostasMedico.sort(Comparator.comparing(Resposta::getIdQuestao));

        for (Resposta r : respostasMedico) {
            String textoQuestao = null;
            BigDecimal maxEscala = null;
            BigDecimal valor = r.getValorNumerico();

            Long idQuestao = r.getIdQuestao();
            Questao questaoEnt = null;
            Long idCriterio = null;

            if (idQuestao != null) {
                var qOpt = questaoService.findById(idQuestao);
                if (qOpt.isPresent()) {
                    questaoEnt = qOpt.get();
                    textoQuestao = questaoEnt.getEnunciado();
                    maxEscala = obterEscalaMax(questaoEnt);
                } else {
                    textoQuestao = "(questão não encontrada)";
                }

                // tenta reaproveitar o mapeamento; se não houver, extrai do objeto
                idCriterio = questaoParaCriterio.get(idQuestao);
                if (idCriterio == null && questaoEnt != null) {
                    idCriterio = extrairIdCriterio(questaoEnt);
                }
            }

            // Min/Max/Média do grupo para ESTA questão (barra horizontal)
            BigDecimal notaGrupoMinQuestao = null;
            BigDecimal notaGrupoMaxQuestao = null;
            BigDecimal notaGrupoMediaQuestao = null;

            if (exibirComparativoGrupo && idQuestao != null) {
                List<BigDecimal> grupoNotas = notasGrupoPorQuestao.get(idQuestao);
                if (grupoNotas != null && !grupoNotas.isEmpty()) {
                    notaGrupoMinQuestao = grupoNotas.stream()
                            .filter(Objects::nonNull)
                            .min(Comparator.naturalOrder())
                            .orElse(null);

                    notaGrupoMaxQuestao = grupoNotas.stream()
                            .filter(Objects::nonNull)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    BigDecimal soma = grupoNotas.stream()
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    notaGrupoMediaQuestao = soma.divide(
                            BigDecimal.valueOf(grupoNotas.size()),
                            2,
                            RoundingMode.HALF_UP
                    );
                }
            }


            BigDecimal notaNormalizada = null;
            if (valor != null && maxEscala != null && maxEscala.compareTo(BigDecimal.ZERO) > 0) {
                notaNormalizada = normalizarPara05(valor, maxEscala);
                notasOriginais.add(valor);
                notasNormalizadasMedico.add(notaNormalizada);

                // agregação por critério (médico)
                if (idCriterio != null && notaNormalizada != null) {
                    somaMedicoPorCriterio.merge(idCriterio, notaNormalizada, BigDecimal::add);
                    qtdeMedicoPorCriterio.merge(idCriterio, 1, Integer::sum);
                }
            }

            // flags de privacidade da questão
            boolean sensivel = questaoEnt != null && Boolean.TRUE.equals(questaoEnt.getSensivel());
            boolean visivelParaGestor = questaoEnt != null && Boolean.TRUE.equals(questaoEnt.getVisivelParaGestor());

            questoes.add(new QuestaoNotaVM(
                    ordem++,
                    valor,
                    maxEscala,
                    textoQuestao,
                    notaGrupoMinQuestao,
                    notaGrupoMaxQuestao,
                    notaGrupoMediaQuestao,
                    sensivel,
                    visivelParaGestor
            ));

        }

        // 5. Nota global do médico (gauge)
        BigDecimal media = calcularNotaGlobalMedico(notasNormalizadasMedico, totalQuestoesNumericas);

        BigDecimal min = null;
        BigDecimal max = null;
        if (!notasOriginais.isEmpty()) {
            min = notasOriginais.stream().min(Comparator.naturalOrder()).orElse(null);
            max = notasOriginais.stream().max(Comparator.naturalOrder()).orElse(null);
        }

        String periodo = "-";
        if (avaliacao.getDataInicioAplic() != null && avaliacao.getDataFimAplic() != null) {
            periodo = avaliacao.getDataInicioAplic().format(DF)
                    + " a "
                    + avaliacao.getDataFimAplic().format(DF);
        }

        StatusVisualVM statusVm = buildStatusVisual(avaliacao.getStatus());

        AvaliacaoMedicoResumoVM cabecalho = new AvaliacaoMedicoResumoVM(
                avaliacao.getIdAvaliacao(),
                avaliacao.getTitulo(),
                periodo,
                statusVm.label(),
                statusVm.badgeClass(),
                idMedico,
                nomeMedico,
                nomeEspecialidade,
                nomeUnidade,
                participacao.getStatus(),
                media,
                min,
                max,
                notasOriginais.size(),
                mediaGrupo,
                menorNotaGp,
                maiorNotaGp,
                exibirComparativoGrupo   // <-- NOVO
        );


        // 6. Monta lista de critérios para o gráfico radar
        List<CriterioRadarVM> criteriosRadar = new ArrayList<>();
        Set<Long> idsCriterios = new HashSet<>();
        idsCriterios.addAll(somaGrupoPorCriterio.keySet());
        idsCriterios.addAll(somaMedicoPorCriterio.keySet());

        for (Long idCriterio : idsCriterios) {
            if (idCriterio == null) continue;

            String nomeCriterio = criterioService.findById(idCriterio)
                    .map(Criterio::getNome)
                    .orElse("(critério não encontrado)");

            BigDecimal notaMedicoCrit = null;
            BigDecimal notaGrupoCrit = null;

            Integer countMedico = qtdeMedicoPorCriterio.get(idCriterio);
            if (countMedico != null && countMedico > 0) {
                notaMedicoCrit = somaMedicoPorCriterio.get(idCriterio)
                        .divide(BigDecimal.valueOf(countMedico), 2, RoundingMode.HALF_UP);
            }

            Integer countGrupo = qtdeGrupoPorCriterio.get(idCriterio);
            if (exibirComparativoGrupo && countGrupo != null && countGrupo > 0) {
                notaGrupoCrit = somaGrupoPorCriterio.get(idCriterio)
                        .divide(BigDecimal.valueOf(countGrupo), 2, RoundingMode.HALF_UP);
            }


            criteriosRadar.add(new CriterioRadarVM(
                    idCriterio,
                    nomeCriterio,
                    notaMedicoCrit,
                    notaGrupoCrit
            ));
        }

        criteriosRadar.sort(Comparator.comparing(CriterioRadarVM::nomeCriterio));





        return new RelatorioAvaliacaoMedicoVM(cabecalho, questoes, criteriosRadar);
    }

    // ==========================================================
    // Helpers
    // ==========================================================

    private int contarQuestoesNumericas(List<Participacao> participacoes) {
        Set<Long> questoesIds = new HashSet<>();
        for (Participacao p : participacoes) {
            List<Resposta> respostas = respostaService.listByParticipacao(p.getIdParticipacao());
            respostas.stream()
                    .filter(r -> r.getValorNumerico() != null)
                    .map(Resposta::getIdQuestao)
                    .filter(Objects::nonNull)
                    .forEach(questoesIds::add);
        }
        return questoesIds.size();
    }

    private BigDecimal obterEscalaMax(Questao q) {
        if (q == null) return null;
        String tipo = q.getTipo();
        if ("LIKERT_5".equalsIgnoreCase(tipo) || "BARS_5".equalsIgnoreCase(tipo)) return BigDecimal.valueOf(5);
        if ("NUM_0_10".equalsIgnoreCase(tipo)) return BigDecimal.TEN;
        if (q.getValidacaoNumMax() != null) return q.getValidacaoNumMax();
        return BigDecimal.valueOf(5);
    }

    private BigDecimal normalizarPara05(BigDecimal valor, BigDecimal maxEscala) {
        if (valor == null || maxEscala == null || maxEscala.compareTo(BigDecimal.ZERO) <= 0) return null;
        return valor.multiply(BigDecimal.valueOf(5))
                .divide(maxEscala, 3, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularNotaGlobalMedico(List<BigDecimal> notasNormalizadas, int totalQuestoesNumericas) {
        if (totalQuestoesNumericas <= 0 || notasNormalizadas == null || notasNormalizadas.isEmpty()) return null;
        BigDecimal soma = notasNormalizadas.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(
                BigDecimal.valueOf(totalQuestoesNumericas),
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Reutiliza a lógica para calcular a nota global de QUALQUER participante
     * para podermos tirar a média do grupo.
     */
    private BigDecimal calcularNotaGlobalParticipacao(Participacao participacao, int totalQuestoesNumericas) {
        if (totalQuestoesNumericas <= 0) return null;

        List<Resposta> respostas = respostaService.listByParticipacao(participacao.getIdParticipacao());
        List<BigDecimal> notasNormalizadas = new ArrayList<>();

        for (Resposta r : respostas) {
            BigDecimal valor = r.getValorNumerico();
            if (valor == null || r.getIdQuestao() == null) continue;

            Questao q = questaoService.findById(r.getIdQuestao()).orElse(null);
            BigDecimal maxEscala = obterEscalaMax(q);
            if (maxEscala == null || maxEscala.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal normalizada = normalizarPara05(valor, maxEscala);
            if (normalizada != null) notasNormalizadas.add(normalizada);
        }

        return calcularNotaGlobalMedico(notasNormalizadas, totalQuestoesNumericas);
    }

    private StatusVisualVM buildStatusVisual(String status) {
        if ("PUBLICADA".equalsIgnoreCase(status))
            return new StatusVisualVM("Publicada", "bg-primary text-white");
        if ("ENCERRADA".equalsIgnoreCase(status))
            return new StatusVisualVM("Encerrada", "bg-secondary text-white");
        return new StatusVisualVM(status, "bg-secondary text-white");
    }

    /**
     * Helper para obter o id do critério a partir da Questao.
     * Ajuste aqui se o nome do getter na entidade for diferente.
     */
    private Long extrairIdCriterio(Questao q) {
        if (q == null) return null;
        // Se na sua entidade o campo for getIdCriterio(), troque esta linha.
        return q.getIdCriterio();
    }
}

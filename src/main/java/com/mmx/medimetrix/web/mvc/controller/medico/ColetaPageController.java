package com.mmx.medimetrix.web.mvc.controller.medico;

import com.mmx.medimetrix.application.avaliacao.service.AvaliacaoService;
import com.mmx.medimetrix.application.avaliacaoquestao.service.AvaliacaoQuestaoService;
import com.mmx.medimetrix.application.medico.service.MedicoService;
import com.mmx.medimetrix.application.questao.service.QuestaoService;
import com.mmx.medimetrix.application.participacao.service.ParticipacaoService;
import com.mmx.medimetrix.application.resposta.commands.RespostaCreate;
import com.mmx.medimetrix.application.resposta.commands.RespostaUpdateValores;
import com.mmx.medimetrix.application.resposta.service.RespostaService;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.domain.core.Avaliacao;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import com.mmx.medimetrix.domain.core.Medico;
import com.mmx.medimetrix.domain.core.Participacao;
import com.mmx.medimetrix.domain.core.Questao;
import com.mmx.medimetrix.domain.core.Resposta;
import com.mmx.medimetrix.domain.core.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/coleta")
public class ColetaPageController {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MedicoService medicoService;
    private final UsuarioService usuarioService;
    private final ParticipacaoService participacaoService;
    private final AvaliacaoService avaliacaoService;
    private final AvaliacaoQuestaoService avaliacaoQuestaoService;
    private final QuestaoService questaoService;
    private final RespostaService respostaService;

    public ColetaPageController(MedicoService medicoService,
                                UsuarioService usuarioService,
                                ParticipacaoService participacaoService,
                                AvaliacaoService avaliacaoService,
                                AvaliacaoQuestaoService avaliacaoQuestaoService,
                                QuestaoService questaoService,
                                RespostaService respostaService) {
        this.medicoService = medicoService;
        this.usuarioService = usuarioService;
        this.participacaoService = participacaoService;
        this.avaliacaoService = avaliacaoService;
        this.avaliacaoQuestaoService = avaliacaoQuestaoService;
        this.questaoService = questaoService;
        this.respostaService = respostaService;
    }

    // ================================
    // Lista "Minhas Avaliações"
    // ================================
    @GetMapping
    public String list(@RequestParam(name = "medicoId", required = false) Long medicoId,
                       @RequestParam(name = "status", required = false) String status,
                       @RequestParam(name = "termo", required = false) String termo,
                       Model model) {

        model.addAttribute("pageTitle", "Minhas Avaliações");
        model.addAttribute("breadcrumb", "Coleta");

        final int BIG_SIZE = 1000;

        // 1) Carrega todos os médicos ativos
        List<Medico> medicosBase = medicoService.listPaged(0, BIG_SIZE);
        List<MedicoSimuladoVM> medicos = new ArrayList<>();

        for (Medico m : medicosBase) {
            try {
                Usuario u = usuarioService.getById(m.getUsuarioId());

                boolean incluir = true;

                // Se houver filtro de status, só inclui médicos que tenham
                // pelo menos uma participação nesse status
                if (status != null && !status.isBlank()) {
                    List<Participacao> partsMed =
                            participacaoService.listByMedico(m.getUsuarioId(), 0, BIG_SIZE);

                    incluir = partsMed.stream()
                            .anyMatch(p -> status.equals(p.getStatus()));
                }

                if (incluir) {
                    medicos.add(new MedicoSimuladoVM(m.getUsuarioId(), u.getNome()));
                }

            } catch (Exception ignore) {
            }
        }

        // Ordena alfabeticamente os médicos que passaram no filtro
        medicos.sort(Comparator.comparing(MedicoSimuladoVM::nome));
        model.addAttribute("medicos", medicos);
        model.addAttribute("status", status);

        // Se não sobrou nenhum médico para esse status, lista vazia
        if (medicos.isEmpty()) {
            model.addAttribute("avaliacoes", List.of());
            model.addAttribute("medicoId", null);
            model.addAttribute("termo", termo);
            return "medico/coleta-list";
        }

        // 2) Define médico selecionado:
        //    - se não veio medicoId, pega o primeiro da lista filtrada
        //    - se veio, mas não está na lista filtrada, também força o primeiro
        Long medicoSelecionadoId = medicoId;

        Long medicoIdForValidation = medicoSelecionadoId; // efetivamente final para usar na lambda
        boolean medicoValido = medicoIdForValidation != null &&
                medicos.stream().anyMatch(m -> m.id().equals(medicoIdForValidation));

        if (!medicoValido) {
            medicoSelecionadoId = medicos.get(0).id();
        }
        model.addAttribute("medicoId", medicoSelecionadoId);

        // 3) Busca participações do médico selecionado
        List<Participacao> participacoes =
                participacaoService.listByMedico(medicoSelecionadoId, 0, BIG_SIZE);

        // Se houver filtro de status, aplica aqui também
        if (status != null && !status.isBlank()) {
            participacoes = participacoes.stream()
                    .filter(p -> status.equals(p.getStatus()))
                    .collect(Collectors.toList());
        }

        // 4) Montar view model de avaliações
        List<MinhaAvaliacaoListItemVM> avaliacoes = participacoes.stream()
                .map(p -> avaliacaoService.findById(p.getIdAvaliacao())
                        .map(a -> MinhaAvaliacaoListItemVM.from(a, p))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 5) Filtro por termo no título
        if (termo != null && !termo.isBlank()) {
            String lower = termo.toLowerCase();
            avaliacoes = avaliacoes.stream()
                    .filter(a -> a.titulo().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        model.addAttribute("termo", termo);
        model.addAttribute("avaliacoes", avaliacoes);

        return "medico/coleta-list";
    }


    // ================================
    // Tela de respostas (GET)
    // ================================
    @GetMapping("/{id}/responder")
    public String responder(@PathVariable Long id,
                            @RequestParam(name = "medicoId") Long medicoId,
                            Model model) {

        Avaliacao avaliacao = avaliacaoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        Participacao participacao = participacaoService.findByAvaliacaoAndMedico(id, medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        // Médico simulado para exibir no cabeçalho
        String medicoNome = null;
        try {
            Usuario u = usuarioService.getById(medicoId);
            medicoNome = u.getNome();
        } catch (Exception ignore) {
        }

        // Carregar questões da avaliação
        List<AvaliacaoQuestao> avaliacaoQuestoes = avaliacaoQuestaoService.listByAvaliacao(id);

        // Carregar respostas existentes da participação
        List<Resposta> respostas = respostaService.listByParticipacao(participacao.getIdParticipacao());
        Map<Long, Resposta> respostaPorQuestao = respostas.stream()
                .collect(Collectors.toMap(Resposta::getIdQuestao, r -> r));

        List<QuestaoRespostaVM> questoes = new ArrayList<>();
        for (AvaliacaoQuestao aq : avaliacaoQuestoes) {
            Optional<Questao> qOpt = questaoService.findById(aq.getIdQuestao());
            if (qOpt.isEmpty()) continue;
            Questao q = qOpt.get();
            Resposta r = respostaPorQuestao.get(q.getIdQuestao());

            Integer valorNumerico = null;
            Boolean valorBinario = null;
            String texto = null;

            if (r != null) {
                if (r.getValorNumerico() != null) {
                    valorNumerico = r.getValorNumerico().intValue();
                }
                valorBinario = r.getValorBinario();
                texto = r.getTexto();
            }

            boolean obrigatoria = "OBRIGATORIA".equalsIgnoreCase(q.getObrigatoriedade());

            questoes.add(new QuestaoRespostaVM(
                    q.getIdQuestao(),
                    aq.getOrdem(),
                    q.getEnunciado(),
                    q.getDescricaoAuxiliar(),
                    q.getTipo(),
                    obrigatoria,
                    (r != null ? r.getIdResposta() : null),
                    valorNumerico,
                    valorBinario,
                    texto
            ));
        }

        // Ordena por ordem da avaliação
        questoes.sort(Comparator.comparing(QuestaoRespostaVM::ordem));

        // Definir se é somente leitura
        boolean apenasLeitura = isSomenteLeitura(avaliacao, participacao);

        // Status da participação para o cabeçalho
        StatusVisualVM statusVm = buildStatusVisual(participacao.getStatus());

        String prazo = avaliacao.getDataFimAplic() != null
                ? avaliacao.getDataFimAplic().format(DF)
                : "—";

        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("participacao", participacao);
        model.addAttribute("questoes", questoes);
        model.addAttribute("apenasLeitura", apenasLeitura);
        model.addAttribute("participacaoStatusLabel", statusVm.label());
        model.addAttribute("participacaoBadgeClass", statusVm.badgeClass());
        model.addAttribute("prazo", prazo);
        model.addAttribute("medicoId", medicoId);
        model.addAttribute("medicoNome", medicoNome);

        return "medico/coleta-form";
    }

    // ================================
    // Salvar rascunho
    // ================================
    @PostMapping("/{id}/responder")
    public String salvarRascunho(@PathVariable Long id,
                                 @RequestParam(name = "medicoId") Long medicoId,
                                 @RequestParam(name = "questaoIds") List<Long> questaoIds,
                                 HttpServletRequest request) {

        Participacao participacao = participacaoService.findByAvaliacaoAndMedico(id, medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        salvarRespostasFromRequest(id, participacao.getIdParticipacao(), questaoIds, request);

        // Atualiza status/timestamp
        if ("PENDENTE".equals(participacao.getStatus())) {
            participacaoService.markStarted(participacao.getIdParticipacao());
        } else {
            participacaoService.touchActivity(participacao.getIdParticipacao());
        }

        return "redirect:/app/coleta/" + id + "/responder?medicoId=" + medicoId;
    }

    // ================================
    // Enviar respostas (marca RESPONDIDA)
    // ================================
    @PostMapping("/{id}/enviar")
    public String enviar(@PathVariable Long id,
                         @RequestParam(name = "medicoId") Long medicoId,
                         @RequestParam(name = "questaoIds") List<Long> questaoIds,
                         HttpServletRequest request,
                         Model model) {

        Participacao participacao = participacaoService.findByAvaliacaoAndMedico(id, medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        // 1) Salva o que veio do formulário (como no rascunho)
        salvarRespostasFromRequest(id, participacao.getIdParticipacao(), questaoIds, request);

        Avaliacao avaliacao = avaliacaoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        // 2) Valida questões obrigatórias
        List<Integer> faltantes = validarQuestoesObrigatorias(id, participacao.getIdParticipacao());
        if (!faltantes.isEmpty()) {

            // Monta mensagem de erro
            String lista = faltantes.stream()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            String msg = "Você precisa responder todas as questões obrigatórias. " +
                    "Questões pendentes: " + lista + ".";

            // Reconstroi a tela de respostas (modo edição)
            String medicoNome = null;
            try {
                Usuario u = usuarioService.getById(medicoId);
                medicoNome = u.getNome();
            } catch (Exception ignore) { }

            // Carregar questões de novo, com as respostas recém-salvas
            List<AvaliacaoQuestao> avaliacaoQuestoes = avaliacaoQuestaoService.listByAvaliacao(id);
            List<Resposta> respostas = respostaService.listByParticipacao(participacao.getIdParticipacao());
            Map<Long, Resposta> respostaPorQuestao = respostas.stream()
                    .collect(Collectors.toMap(Resposta::getIdQuestao, r -> r, (r1, r2) -> r1));

            List<QuestaoRespostaVM> questoes = new ArrayList<>();
            for (AvaliacaoQuestao aq : avaliacaoQuestoes) {
                Optional<Questao> qOpt = questaoService.findById(aq.getIdQuestao());
                if (qOpt.isEmpty()) continue;
                Questao q = qOpt.get();
                Resposta r = respostaPorQuestao.get(q.getIdQuestao());

                Integer valorNumerico = null;
                Boolean valorBinario = null;
                String texto = null;

                if (r != null) {
                    if (r.getValorNumerico() != null) {
                        valorNumerico = r.getValorNumerico().intValue();
                    }
                    valorBinario = r.getValorBinario();
                    texto = r.getTexto();
                }

                boolean obrigatoria = "OBRIGATORIA".equalsIgnoreCase(q.getObrigatoriedade());

                questoes.add(new QuestaoRespostaVM(
                        q.getIdQuestao(),
                        aq.getOrdem(),
                        q.getEnunciado(),
                        q.getDescricaoAuxiliar(),
                        q.getTipo(),
                        obrigatoria,
                        (r != null ? r.getIdResposta() : null),
                        valorNumerico,
                        valorBinario,
                        texto
                ));
            }
            questoes.sort(Comparator.comparing(QuestaoRespostaVM::ordem));

            StatusVisualVM statusVm = buildStatusVisual(participacao.getStatus());
            String prazo = avaliacao.getDataFimAplic() != null
                    ? avaliacao.getDataFimAplic().format(DF)
                    : "—";

            model.addAttribute("avaliacao", avaliacao);
            model.addAttribute("participacao", participacao);
            model.addAttribute("questoes", questoes);
            model.addAttribute("apenasLeitura", false); // ainda pode editar
            model.addAttribute("participacaoStatusLabel", statusVm.label());
            model.addAttribute("participacaoBadgeClass", statusVm.badgeClass());
            model.addAttribute("prazo", prazo);
            model.addAttribute("medicoId", medicoId);
            model.addAttribute("medicoNome", medicoNome);
            model.addAttribute("validationError", msg);

            return "medico/coleta-form";
        }

        // 3) Se passou na validação, marca como enviada e vai para o comprovante
        participacaoService.markSubmitted(participacao.getIdParticipacao());

        String medicoNome = null;
        try {
            Usuario u = usuarioService.getById(medicoId);
            medicoNome = u.getNome();
        } catch (Exception ignore) { }

        model.addAttribute("pageTitle", "Comprovante");
        model.addAttribute("breadcrumb", "Coleta");
        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("medicoId", medicoId);
        model.addAttribute("medicoNome", medicoNome);

        return "medico/coleta-comprovante";
    }


    // ================================
    // Helpers internos
    // ================================
    private void salvarRespostasFromRequest(Long idAvaliacao,
                                            Long idParticipacao,
                                            List<Long> questaoIds,
                                            HttpServletRequest request) {

        // Carregar respostas atuais para decidir entre create/update
        List<Resposta> existentes = respostaService.listByParticipacao(idParticipacao);
        Map<Long, Resposta> respostaPorQuestao = existentes.stream()
                .collect(Collectors.toMap(Resposta::getIdQuestao, r -> r));

        for (Long idQuestao : questaoIds) {
            String paramPrefixNum = "valorNumerico__" + idQuestao;
            String paramPrefixTxt = "texto__" + idQuestao;
            String paramPrefixBin = "valorBinario__" + idQuestao;

            String strNum = request.getParameter(paramPrefixNum);
            String texto = request.getParameter(paramPrefixTxt);
            String binStr = request.getParameter(paramPrefixBin);

            BigDecimal valorNumerico = null;
            if (strNum != null && !strNum.isBlank()) {
                try {
                    valorNumerico = new BigDecimal(strNum);
                } catch (NumberFormatException ignore) {
                }
            }

            Boolean valorBinario = null;
            if (binStr != null) { // checkbox marcado
                valorBinario = Boolean.TRUE;
            }

            // Se nada foi preenchido, pula (mantém o que já estava no banco)
            if (valorNumerico == null && valorBinario == null && (texto == null || texto.isBlank())) {
                continue;
            }

            Resposta existente = respostaPorQuestao.get(idQuestao);
            if (existente == null) {
                RespostaCreate cmd = new RespostaCreate();
                cmd.setIdParticipacao(idParticipacao);
                cmd.setIdAvaliacao(idAvaliacao);
                cmd.setIdQuestao(idQuestao);
                cmd.setValorNumerico(valorNumerico);
                cmd.setValorBinario(valorBinario);
                cmd.setTexto(texto);
                respostaService.create(cmd);
            } else {
                RespostaUpdateValores cmd = new RespostaUpdateValores();
                cmd.setValorNumerico(valorNumerico);
                cmd.setValorBinario(valorBinario);
                cmd.setTexto(texto);
                respostaService.updateValores(existente.getIdResposta(), cmd);
            }
        }
    }

    /**
     * Retorna a lista de ORDENS das questões obrigatórias que não foram respondidas.
     */
    private List<Integer> validarQuestoesObrigatorias(Long idAvaliacao, Long idParticipacao) {

        List<AvaliacaoQuestao> avaliacaoQuestoes = avaliacaoQuestaoService.listByAvaliacao(idAvaliacao);
        List<Resposta> respostas = respostaService.listByParticipacao(idParticipacao);

        Map<Long, Resposta> respostaPorQuestao = respostas.stream()
                .collect(Collectors.toMap(Resposta::getIdQuestao, r -> r, (r1, r2) -> r1));

        List<Integer> faltantes = new ArrayList<>();

        for (AvaliacaoQuestao aq : avaliacaoQuestoes) {
            Optional<Questao> qOpt = questaoService.findById(aq.getIdQuestao());
            if (qOpt.isEmpty()) {
                continue;
            }
            Questao q = qOpt.get();

            // só interessa se for obrigatória
            if (!"OBRIGATORIA".equalsIgnoreCase(q.getObrigatoriedade())) {
                continue;
            }

            Resposta r = respostaPorQuestao.get(q.getIdQuestao());
            boolean respondida = false;

            if (r != null) {
                String tipo = q.getTipo();
                if ("LIKERT_5".equals(tipo) || "BARS_5".equals(tipo) || "NUM_0_10".equals(tipo)) {
                    respondida = r.getValorNumerico() != null;
                } else if ("OPEN".equals(tipo)) {
                    respondida = r.getTexto() != null && !r.getTexto().isBlank();
                } else if ("CHECK".equals(tipo)) {
                    // para CHECK, consideramos respondida se o valor está definido (true)
                    respondida = r.getValorBinario() != null;
                } else {
                    // fallback: qualquer valor já conta como resposta
                    respondida =
                            r.getValorNumerico() != null ||
                                    r.getValorBinario() != null ||
                                    (r.getTexto() != null && !r.getTexto().isBlank());
                }
            }

            if (!respondida) {
                faltantes.add(aq.getOrdem());
            }
        }

        return faltantes;
    }



    private boolean isSomenteLeitura(Avaliacao avaliacao, Participacao participacao) {
        String statusPart = participacao.getStatus();
        if ("RESPONDIDA".equals(statusPart)) {
            return true;
        }

        LocalDate hoje = LocalDate.now();
        if (avaliacao.getDataFimAplic() != null && avaliacao.getDataFimAplic().isBefore(hoje)) {
            return true;
        }

        if ("ENCERRADA".equals(avaliacao.getStatus())) {
            return true;
        }

        return false;
    }

    private StatusVisualVM buildStatusVisual(String status) {
        if ("PENDENTE".equals(status)) {
            return new StatusVisualVM("Não iniciada", "text-bg-secondary");
        }
        if ("EM_ANDAMENTO".equals(status)) {
            return new StatusVisualVM("Em andamento", "text-bg-warning");
        }
        if ("RESPONDIDA".equals(status)) {
            return new StatusVisualVM("Respondida", "text-bg-success");
        }
        return new StatusVisualVM(status, "text-bg-secondary");
    }

    // ================================
    // View Models
    // ================================
    public record MedicoSimuladoVM(Long id, String nome) {}

    public record MinhaAvaliacaoListItemVM(
            Long avaliacaoId,
            Long participacaoId,
            String titulo,
            String prazo,
            String situacaoLabel,
            String situacaoBadgeClass,
            String acaoLabel,
            boolean somenteLeitura
    ) {
        public static MinhaAvaliacaoListItemVM from(Avaliacao a, Participacao p) {
            String prazo = (a.getDataFimAplic() != null)
                    ? a.getDataFimAplic().format(DF)
                    : "—";

            String status = p.getStatus();

            StatusVisualVM statusVm;
            if ("PENDENTE".equals(status)) {
                statusVm = new StatusVisualVM("Não iniciada", "text-bg-secondary");
            } else if ("EM_ANDAMENTO".equals(status)) {
                statusVm = new StatusVisualVM("Em andamento", "text-bg-warning");
            } else if ("RESPONDIDA".equals(status)) {
                statusVm = new StatusVisualVM("Respondida", "text-bg-success");
            } else {
                statusVm = new StatusVisualVM(status, "text-bg-secondary");
            }

            // Regras de somente leitura
            boolean somenteLeitura = false;
            if ("RESPONDIDA".equals(status)) {
                somenteLeitura = true;
            }
            if (a.getDataFimAplic() != null && a.getDataFimAplic().isBefore(java.time.LocalDate.now())) {
                somenteLeitura = true;
            }
            if ("ENCERRADA".equals(a.getStatus())) {
                somenteLeitura = true;
            }

            // Label do botão
            String acao;
            if (somenteLeitura) {
                acao = "Visualizar";
            } else if ("PENDENTE".equals(status)) {
                acao = "Responder";
            } else {
                acao = "Continuar";
            }

            return new MinhaAvaliacaoListItemVM(
                    a.getIdAvaliacao(),
                    p.getIdParticipacao(),
                    a.getTitulo(),
                    prazo,
                    statusVm.label(),
                    statusVm.badgeClass(),
                    acao,
                    somenteLeitura
            );
        }
    }


    public record QuestaoRespostaVM(
            Long idQuestao,
            Integer ordem,
            String enunciado,
            String descricaoAuxiliar,
            String tipo,
            boolean obrigatoria,
            Long idResposta,
            Integer valorNumerico,
            Boolean valorBinario,
            String texto
    ) {}

    public record StatusVisualVM(String label, String badgeClass) {}
}

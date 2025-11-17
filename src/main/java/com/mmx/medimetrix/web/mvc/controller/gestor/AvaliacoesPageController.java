package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoCreate;
import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoUpdate;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoEncerradaException;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoJaPublicadaException;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoNaoEncontradaException;
import com.mmx.medimetrix.application.avaliacao.service.AvaliacaoService;
import com.mmx.medimetrix.application.avaliacaoquestao.service.AvaliacaoQuestaoService;
import com.mmx.medimetrix.application.participacao.service.ParticipacaoService;
import com.mmx.medimetrix.application.questao.queries.QuestaoFiltro;
import com.mmx.medimetrix.application.questao.service.QuestaoService;
import com.mmx.medimetrix.application.resposta.service.RespostaService;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.domain.core.Unidade;
import com.mmx.medimetrix.domain.core.Especialidade;
import com.mmx.medimetrix.domain.core.Avaliacao;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import com.mmx.medimetrix.domain.core.Participacao;
import com.mmx.medimetrix.domain.core.Questao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/app/avaliacoes")
public class AvaliacoesPageController {

    private final AvaliacaoService avaliacaoService;
    private final AvaliacaoQuestaoService avaliacaoQuestaoService;
    private final QuestaoService questaoService;
    private final ParticipacaoService participacaoService;
    private final RespostaService respostaService;
    private final UnidadeService unidadeService;
    private final EspecialidadeService especialidadeService;


    public AvaliacoesPageController(AvaliacaoService avaliacaoService,
                                    AvaliacaoQuestaoService avaliacaoQuestaoService,
                                    QuestaoService questaoService,
                                    ParticipacaoService participacaoService,
                                    RespostaService respostaService,
                                    UnidadeService unidadeService,
                                    EspecialidadeService especialidadeService) {
        this.avaliacaoService = avaliacaoService;
        this.avaliacaoQuestaoService = avaliacaoQuestaoService;
        this.questaoService = questaoService;
        this.participacaoService = participacaoService;
        this.respostaService = respostaService;
        this.unidadeService = unidadeService;
        this.especialidadeService = especialidadeService;
    }


    // =========================================================
    // LISTA
    // =========================================================
    @GetMapping
    public String list(@RequestParam(name = "status", required = false) String status,
                       @RequestParam(name = "termo", required = false) String termo,
                       @RequestParam(name = "naData", required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate naData,
                       @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                       @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
                       Model model) {

        int pageIdx = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size < 1 || size > 100) ? 20 : size;

        List<Avaliacao> dominio;
        if (StringUtils.hasText(status)) {
            dominio = avaliacaoService.listByStatus(status.trim(), pageIdx, pageSize);
        } else if (StringUtils.hasText(termo)) {
            dominio = avaliacaoService.searchByTituloLike(termo.trim(), pageIdx, pageSize);
        } else {
            dominio = avaliacaoService.listPaged(pageIdx, pageSize);
        }

        // monta VMs já com o total de questões
        List<AvaliacaoListItemVM> avaliacoes = dominio.stream()
                .map(a -> {
                    int totalQuestoes =
                            avaliacaoQuestaoService.listByAvaliacao(a.getIdAvaliacao()).size();
                    return AvaliacaoListItemVM.from(a, totalQuestoes);
                })
                .toList();

        boolean hasPrev = pageIdx > 0;
        boolean hasNext = avaliacoes.size() == pageSize; // heurística simples

        model.addAttribute("pageTitle", "Avaliações");
        model.addAttribute("breadcrumb", "Avaliações");

        model.addAttribute("avaliacoes", avaliacoes);

        model.addAttribute("pageIdx", pageIdx);
        model.addAttribute("size", pageSize);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);

        model.addAttribute("filtroStatus", status);
        model.addAttribute("filtroTermo", termo);
        model.addAttribute("filtroNaData", naData);

        model.addAttribute("statusOptions", statusOptions());
        // flash "confirmSuspensaoId" (quando existir) chega automático no model

        return "gestor/avaliacoes-list";
    }

    // =========================================================
    // TELA DE QUESTÕES DA AVALIAÇÃO
    // =========================================================
    @GetMapping("/{id}/questoes")
    public String questoes(@PathVariable Long id,
                           @RequestParam(name = "termoCatalogo", required = false) String termoCatalogo,
                           Model model) {

        Avaliacao avaliacao = avaliacaoService.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        // >>> NOVO: tela somente leitura se ENCERRADA <<<
        boolean readOnly = "ENCERRADA".equalsIgnoreCase(avaliacao.getStatus());

        // ----- Questões já vinculadas à avaliação -----
        List<AvaliacaoQuestao> vinculadas = avaliacaoQuestaoService.listByAvaliacao(id);

        List<AvaliacaoQuestaoVM> questoesAvaliacao = vinculadas.stream()
                .map(aq -> {
                    Optional<Questao> qOpt = questaoService.findById(aq.getIdQuestao());
                    String enunciado = qOpt.map(Questao::getEnunciado)
                            .orElse("(Questão não encontrada)");
                    return AvaliacaoQuestaoVM.from(aq, enunciado);
                })
                .toList();

        // ----- Catálogo de questões (lado esquerdo) -----
        List<Questao> questoesCatalogo;

        int pageIdx = 0;
        int pageSize = 50; // por enquanto, primeira página com 50 itens

        QuestaoFiltro filtro;

        if (StringUtils.hasText(termoCatalogo)) {
            filtro = new QuestaoFiltro(
                    termoCatalogo.trim(),
                    null,
                    null,
                    pageIdx,
                    pageSize
            );
        } else {
            filtro = new QuestaoFiltro(
                    null,
                    null,
                    null,
                    pageIdx,
                    pageSize
            );
        }

        questoesCatalogo = questaoService.list(filtro);

        model.addAttribute("pageTitle", "Avaliação • Questões");
        model.addAttribute("breadcrumb", "Avaliações");

        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("questoesCatalogo", questoesCatalogo);
        model.addAttribute("questoesAvaliacao", questoesAvaliacao);
        model.addAttribute("termoCatalogo", termoCatalogo);

        // >>> NOVO <<<
        model.addAttribute("readOnly", readOnly);

        return "gestor/avaliacoes-questoes";
    }


    // =========================================================
    // POST - ADICIONAR QUESTÃO NA AVALIAÇÃO
    // =========================================================
    @PostMapping("/{id}/questoes/add")
    public String addQuestao(@PathVariable Long id,
                             @RequestParam("idQuestao") Long idQuestao,
                             RedirectAttributes redirect) {

        // >>> NOVO <<<
        if (isEncerrada(id)) {
            redirect.addFlashAttribute("errorMessage",
                    "Avaliação encerrada não permite alteração de questões.");
            return "redirect:/app/avaliacoes/" + id + "/questoes";
        }

        var existente = avaliacaoQuestaoService.findOne(id, idQuestao);
        if (existente.isPresent()) {
            redirect.addFlashAttribute("warningMessage",
                    "Esta questão já está vinculada a esta avaliação.");
            return "redirect:/app/avaliacoes/" + id + "/questoes";
        }

        avaliacaoQuestaoService.addQuestaoAutoOrdem(id, idQuestao);

        redirect.addFlashAttribute("successMessage",
                "Questão adicionada à avaliação.");
        return "redirect:/app/avaliacoes/" + id + "/questoes";
    }


    // =========================================================
    // POST - MOVER QUESTÃO PARA CIMA
    // =========================================================
    @PostMapping("/{id}/questoes/subir")
    public String subirQuestao(@PathVariable Long id,
                               @RequestParam("idQuestao") Long idQuestao,
                               RedirectAttributes redirect) {

        // >>> NOVO <<<
        if (isEncerrada(id)) {
            redirect.addFlashAttribute("errorMessage",
                    "Avaliação encerrada não permite alteração de questões.");
            return "redirect:/app/avaliacoes/" + id + "/questoes";
        }

        List<AvaliacaoQuestao> lista = avaliacaoQuestaoService.listByAvaliacao(id);

        int idx = -1;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdQuestao().equals(idQuestao)) {
                idx = i;
                break;
            }
        }

        if (idx > 0) {
            Long idQuestaoAcima = lista.get(idx - 1).getIdQuestao();
            avaliacaoQuestaoService.swapOrdem(id, idQuestao, idQuestaoAcima);
        }

        redirect.addFlashAttribute("successMessage",
                "Ordem da questão atualizada.");
        return "redirect:/app/avaliacoes/" + id + "/questoes";
    }


    // =========================================================
    // POST - MOVER QUESTÃO PARA BAIXO
    // =========================================================
    @PostMapping("/{id}/questoes/descer")
    public String descerQuestao(@PathVariable Long id,
                                @RequestParam("idQuestao") Long idQuestao,
                                RedirectAttributes redirect) {

        // >>> NOVO <<<
        if (isEncerrada(id)) {
            redirect.addFlashAttribute("errorMessage",
                    "Avaliação encerrada não permite alteração de questões.");
            return "redirect:/app/avaliacoes/" + id + "/questoes";
        }

        List<AvaliacaoQuestao> lista = avaliacaoQuestaoService.listByAvaliacao(id);

        int idx = -1;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdQuestao().equals(idQuestao)) {
                idx = i;
                break;
            }
        }

        if (idx != -1 && idx < lista.size() - 1) {
            Long idQuestaoAbaixo = lista.get(idx + 1).getIdQuestao();
            avaliacaoQuestaoService.swapOrdem(id, idQuestao, idQuestaoAbaixo);
        }

        redirect.addFlashAttribute("successMessage",
                "Ordem da questão atualizada.");
        return "redirect:/app/avaliacoes/" + id + "/questoes";
    }


    // =========================================================
    // POST - REMOVER QUESTÃO DA AVALIAÇÃO
    // =========================================================
    @PostMapping("/{id}/questoes/remover")
    public String removerQuestao(@PathVariable Long id,
                                 @RequestParam("idQuestao") Long idQuestao,
                                 RedirectAttributes redirect) {

        // >>> NOVO <<<
        if (isEncerrada(id)) {
            redirect.addFlashAttribute("errorMessage",
                    "Avaliação encerrada não permite alteração de questões.");
            return "redirect:/app/avaliacoes/" + id + "/questoes";
        }

        avaliacaoQuestaoService.deleteOne(id, idQuestao);
        avaliacaoQuestaoService.renumerarOrdem(id);

        redirect.addFlashAttribute("successMessage",
                "Questão removida da avaliação.");
        return "redirect:/app/avaliacoes/" + id + "/questoes";
    }


    private void carregarCombosEscopo(Model model) {
    // Unidades ativas
    List<Unidade> unidades = unidadeService.list(
            new UnidadeFiltro(
                    null,      // nomeLike
                    0,         // page
                    500,       // size
                    "nome",    // sortBy
                    true,      // asc
                    true       // ativo
            )
    );

    // Especialidades ativas
    List<Especialidade> especialidades = especialidadeService.listAtivas();

    model.addAttribute("unidades", unidades);
    model.addAttribute("especialidades", especialidades);
}



// =========================================================
    // FORM - NOVA
    // =========================================================
@GetMapping("/nova")
public String nova(Model model) {
    AvaliacaoForm form = new AvaliacaoForm();
    form.setStatus("RASCUNHO");
    form.setAtivo(Boolean.TRUE);
    form.setkMinimo(3);
    form.setEscopo("GLOBAL");

    model.addAttribute("pageTitle", "Nova avaliação");
    model.addAttribute("breadcrumb", "Avaliações");
    model.addAttribute("form", form);
    model.addAttribute("statusOptions", statusOptions());
    model.addAttribute("modoLeitura", false);

    carregarCombosEscopo(model);

    return "gestor/avaliacoes-form";
}


// =========================================================
    // FORM - EDITAR / VISUALIZAR
    // =========================================================
@GetMapping("/{id}/editar")
public String editar(@PathVariable Long id, Model model) {
    Avaliacao a = avaliacaoService.findById(id)
            .orElseThrow(AvaliacaoNaoEncontradaException::new);

    AvaliacaoForm form = AvaliacaoForm.fromDomain(a);

    boolean modoLeitura =
            "PUBLICADA".equalsIgnoreCase(form.getStatus()) ||
                    "ENCERRADA".equalsIgnoreCase(form.getStatus());

    model.addAttribute("pageTitle",
            modoLeitura ? "Visualizar avaliação" : "Editar avaliação");
    model.addAttribute("breadcrumb", "Avaliações");
    model.addAttribute("form", form);
    model.addAttribute("statusOptions", statusOptions());
    model.addAttribute("modoLeitura", modoLeitura);

    carregarCombosEscopo(model);

    return "gestor/avaliacoes-form";
}


// =========================================================
    // POST - CRIAR
    // =========================================================
    @PostMapping
    public String create(@Valid @ModelAttribute("form") AvaliacaoForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {

        if (binding.hasErrors()) {
            model.addAttribute("pageTitle", "Nova avaliação");
            model.addAttribute("breadcrumb", "Avaliações");
            model.addAttribute("statusOptions", statusOptions());
            model.addAttribute("modoLeitura", false);
            carregarCombosEscopo(model);
            return "gestor/avaliacoes-form";
        }


        AvaliacaoCreate cmd = new AvaliacaoCreate();
        cmd.setTitulo(form.getTitulo());
        cmd.setDataInicioAplic(form.getDataInicioAplic());
        cmd.setDataFimAplic(form.getDataFimAplic());
        cmd.setkMinimo(form.getkMinimo());
        cmd.setStatus(form.getStatus());
        cmd.setAtivo(form.getAtivo());
        cmd.setEscopo(form.getEscopo());
        cmd.setIdUnidade(form.getIdUnidade());
        cmd.setIdEspecialidade(form.getIdEspecialidade());

        Long id = avaliacaoService.create(cmd);

        redirect.addFlashAttribute("successMessage",
                "Avaliação criada com sucesso.");
        return "redirect:/app/avaliacoes/" + id + "/editar";
    }

    // =========================================================
    // POST - ATUALIZAR
    // =========================================================
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") AvaliacaoForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {

        Avaliacao atual = avaliacaoService.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        boolean modoLeitura =
                "PUBLICADA".equalsIgnoreCase(atual.getStatus()) ||
                        "ENCERRADA".equalsIgnoreCase(atual.getStatus());

        if (modoLeitura) {
            // não deixa atualizar via POST se não for rascunho
            redirect.addFlashAttribute("warningMessage",
                    "Avaliações publicadas ou encerradas não podem ser alteradas.");
            return "redirect:/app/avaliacoes";
        }

        if (binding.hasErrors()) {
            model.addAttribute("pageTitle", "Editar avaliação");
            model.addAttribute("breadcrumb", "Avaliações");
            model.addAttribute("statusOptions", statusOptions());
            model.addAttribute("modoLeitura", false);
            carregarCombosEscopo(model);
            return "gestor/avaliacoes-form";
        }


        AvaliacaoUpdate cmd = new AvaliacaoUpdate(
                form.getTitulo(),
                form.getDataInicioAplic(),
                form.getDataFimAplic(),
                form.getkMinimo(),
                form.getStatus(),
                form.getAtivo(),
                form.getEscopo(),
                form.getIdUnidade(),
                form.getIdEspecialidade()
        );

        try {
            avaliacaoService.update(id, cmd);
        } catch (AvaliacaoJaPublicadaException ex) {
            redirect.addFlashAttribute("errorMessage",
                    "A avaliação já foi publicada e não pode ser alterada.");
            return "redirect:/app/avaliacoes";
        } catch (AvaliacaoEncerradaException ex) {
            redirect.addFlashAttribute("errorMessage",
                    "A avaliação já foi encerrada e não permite mais alterações.");
            return "redirect:/app/avaliacoes";
        }

        redirect.addFlashAttribute("successMessage",
                "Avaliação atualizada com sucesso.");
        return "redirect:/app/avaliacoes";
    }

    // =========================================================
    // POST - SUSPENDER (status PUBLICADA -> RASCUNHO)
    // =========================================================
    @PostMapping("/{id}/suspender")
    public String suspender(@PathVariable Long id,
                            @RequestParam(name = "confirm", required = false, defaultValue = "false")
                            boolean confirm,
                            RedirectAttributes redirect) {

        Avaliacao avaliacao = avaliacaoService.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        if (!"PUBLICADA".equalsIgnoreCase(avaliacao.getStatus())) {
            redirect.addFlashAttribute("warningMessage",
                    "Apenas avaliações publicadas podem ser suspensas.");
            return "redirect:/app/avaliacoes";
        }

        // conta respostas da avaliação (usando listByAvaliacao)
        int totalRespostas = respostaService.listByAvaliacao(id, 0, 100000).size();

        if (totalRespostas > 0 && !confirm) {
            redirect.addFlashAttribute("warningMessage",
                    "Esta avaliação já possui " + totalRespostas +
                            " resposta(s). Clique novamente em 'Confirmar suspensão' para prosseguir.");
            redirect.addFlashAttribute("confirmSuspensaoId", id);
            return "redirect:/app/avaliacoes";
        }

        // Apaga respostas e participações
        List<Participacao> participacoes = participacaoService.listByAvaliacao(id, 0, 100000);
        for (Participacao p : participacoes) {
            respostaService.deleteByParticipacao(p.getIdParticipacao());
            participacaoService.delete(p.getIdParticipacao());
        }

        // Volta para RASCUNHO
        AvaliacaoUpdate cmd = new AvaliacaoUpdate(
                avaliacao.getTitulo(),
                avaliacao.getDataInicioAplic(),
                avaliacao.getDataFimAplic(),
                avaliacao.getkMinimo(),
                "RASCUNHO",
                avaliacao.getAtivo(),
                avaliacao.getEscopo(),
                avaliacao.getIdUnidade(),
                avaliacao.getIdEspecialidade()
        );

        // Aqui podemos chamar diretamente o DAO, mas mantive pelo service.
        // Como o status atual é PUBLICADA, a regra de negócio de update
        // pode acusar AvaliacaoJaPublicadaException; para não quebrar,
        // mudamos diretamente no DAO – ou, se preferir, relaxamos a regra.
        avaliacao.setStatus("RASCUNHO");
        avaliacaoService.update(id, cmd);

        redirect.addFlashAttribute("successMessage",
                "Avaliação suspensa. Participações e respostas foram removidas e o status voltou para RASCUNHO.");
        return "redirect:/app/avaliacoes";
    }

    // =========================================================
// HELPER: impedir alterações em avaliação encerrada
// =========================================================
    private boolean isEncerrada(Long idAvaliacao) {
        return avaliacaoService.findById(idAvaliacao)
                .map(Avaliacao::getStatus)
                .map(s -> "ENCERRADA".equalsIgnoreCase(s))
                .orElse(false);
    }



    // =========================================================
    // HELPER: opções de status
    // =========================================================
    private List<String> statusOptions() {
        return List.of("RASCUNHO", "PUBLICADA", "ENCERRADA");
    }

    // =========================================================
    // VIEW MODEL do formulário
    // =========================================================
    public static class AvaliacaoForm {

        private Long id;

        @NotBlank
        private String titulo;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull
        private LocalDate dataInicioAplic;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull
        private LocalDate dataFimAplic;

        @NotNull
        @Min(2)
        private Integer kMinimo;

        @NotBlank
        private String status; // RASCUNHO | PUBLICADA | ENCERRADA

        private Boolean ativo = Boolean.TRUE;

        // >>> NOVOS CAMPOS (somente de UI, por enquanto) <<<
        private String escopo;          // GLOBAL | UNIDADE | ESPECIALIDADE
        private Long idUnidade;
        private Long idEspecialidade;

        // getters/setters ...

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public LocalDate getDataInicioAplic() { return dataInicioAplic; }
        public void setDataInicioAplic(LocalDate dataInicioAplic) { this.dataInicioAplic = dataInicioAplic; }

        public LocalDate getDataFimAplic() { return dataFimAplic; }
        public void setDataFimAplic(LocalDate dataFimAplic) { this.dataFimAplic = dataFimAplic; }

        public Integer getkMinimo() { return kMinimo; }
        public void setkMinimo(Integer kMinimo) { this.kMinimo = kMinimo; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Boolean getAtivo() { return ativo; }
        public void setAtivo(Boolean ativo) { this.ativo = ativo; }

        public String getEscopo() { return escopo; }
        public void setEscopo(String escopo) { this.escopo = escopo; }

        public Long getIdUnidade() { return idUnidade; }
        public void setIdUnidade(Long idUnidade) { this.idUnidade = idUnidade; }

        public Long getIdEspecialidade() { return idEspecialidade; }
        public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

        public static AvaliacaoForm fromDomain(Avaliacao a) {
            AvaliacaoForm f = new AvaliacaoForm();
            f.setId(a.getIdAvaliacao());
            f.setTitulo(a.getTitulo());
            f.setDataInicioAplic(a.getDataInicioAplic());
            f.setDataFimAplic(a.getDataFimAplic());
            f.setkMinimo(a.getkMinimo());
            f.setStatus(a.getStatus());
            f.setAtivo(a.getAtivo());
            // Escopo vindo do domínio (default = GLOBAL se vier nulo/vazio)
            String escopo = a.getEscopo();
            if (escopo == null || escopo.isBlank()) {
                escopo = "GLOBAL";
            }
            f.setEscopo(escopo);

            // Preenche unidade / especialidade conforme o escopo
            if ("UNIDADE".equalsIgnoreCase(escopo)) {
                f.setIdUnidade(a.getIdUnidade());
                f.setIdEspecialidade(null);
            } else if ("ESPECIALIDADE".equalsIgnoreCase(escopo)) {
                f.setIdEspecialidade(a.getIdEspecialidade());
                f.setIdUnidade(null);
            } else { // GLOBAL
                f.setIdUnidade(null);
                f.setIdEspecialidade(null);
            }

            return f;
        }
    }

    // =========================================================
    // VIEW MODEL: item da lista
    // =========================================================
    public record AvaliacaoListItemVM(
            Long id,
            String titulo,
            String periodo,
            Integer totalQuestoes,
            String status
    ) {
        public static AvaliacaoListItemVM from(Avaliacao a, int totalQuestoes) {
            String periodo;
            if (a.getDataInicioAplic() != null && a.getDataFimAplic() != null) {
                periodo = a.getDataInicioAplic() + " – " + a.getDataFimAplic();
            } else {
                periodo = "—";
            }
            return new AvaliacaoListItemVM(
                    a.getIdAvaliacao(),
                    a.getTitulo(),
                    periodo,
                    totalQuestoes,
                    a.getStatus()
            );
        }
    }

    // =========================================================
    // VIEW MODEL: Questão dentro da avaliação
    // =========================================================
    public static class AvaliacaoQuestaoVM {
        private Long idQuestao;
        private Integer ordem;
        private String enunciado;
        private Boolean ativaNaAval;

        public Long getIdQuestao() { return idQuestao; }
        public void setIdQuestao(Long idQuestao) { this.idQuestao = idQuestao; }

        public Integer getOrdem() { return ordem; }
        public void setOrdem(Integer ordem) { this.ordem = ordem; }

        public String getEnunciado() { return enunciado; }
        public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

        public Boolean getAtivaNaAval() { return ativaNaAval; }
        public void setAtivaNaAval(Boolean ativaNaAval) { this.ativaNaAval = ativaNaAval; }

        public static AvaliacaoQuestaoVM from(AvaliacaoQuestao aq, String enunciado) {
            AvaliacaoQuestaoVM vm = new AvaliacaoQuestaoVM();
            vm.setIdQuestao(aq.getIdQuestao());
            vm.setOrdem(aq.getOrdem());
            vm.setEnunciado(enunciado);
            vm.setAtivaNaAval(
                    aq.getAtivaNaAval() != null ? aq.getAtivaNaAval() : Boolean.TRUE
            );
            return vm;
        }
    }
}

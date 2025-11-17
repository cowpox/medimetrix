package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoCreate;
import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoUpdate;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoNaoEncontradaException;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoJaPublicadaException;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoEncerradaException;
import com.mmx.medimetrix.application.avaliacao.service.AvaliacaoService;
import com.mmx.medimetrix.application.avaliacaoquestao.service.AvaliacaoQuestaoService;
import com.mmx.medimetrix.application.questao.queries.QuestaoFiltro;
import com.mmx.medimetrix.application.questao.service.QuestaoService;
import com.mmx.medimetrix.domain.core.Avaliacao;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import com.mmx.medimetrix.domain.core.Questao;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;
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
    private final UnidadeService unidadeService;
    private final EspecialidadeService especialidadeService;

    public AvaliacoesPageController(AvaliacaoService avaliacaoService,
                                    AvaliacaoQuestaoService avaliacaoQuestaoService,
                                    QuestaoService questaoService,
                                    UnidadeService unidadeService,
                                    EspecialidadeService especialidadeService) {
        this.avaliacaoService = avaliacaoService;
        this.avaliacaoQuestaoService = avaliacaoQuestaoService;
        this.questaoService = questaoService;
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
            // filtra por enunciado
            filtro = new QuestaoFiltro(
                    termoCatalogo.trim(), // enunciadoLike
                    null,                 // tipo
                    null,                 // idCriterio
                    pageIdx,              // page
                    pageSize              // size
            );
        } else {
            // sem filtro de enunciado, só paginação
            filtro = new QuestaoFiltro(
                    null, // enunciadoLike
                    null, // tipo
                    null, // idCriterio
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

        return "gestor/avaliacoes-questoes";
    }

    // =========================================================
// POST - ADICIONAR QUESTÃO NA AVALIAÇÃO
// =========================================================
    @PostMapping("/{id}/questoes/add")
    public String addQuestao(@PathVariable Long id,
                             @RequestParam("idQuestao") Long idQuestao,
                             RedirectAttributes redirect) {

        // Se já existe o vínculo, não tenta inserir de novo
        var existente = avaliacaoQuestaoService.findOne(id, idQuestao);
        if (existente.isPresent()) {
            redirect.addFlashAttribute("warningMessage",
                    "Esta questão já está vinculada a esta avaliação.");
            return "redirect:/app/avaliacoes/" + id + "/questoes";
        }

        // Usa helper que já define a próxima ordem automaticamente
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

        List<AvaliacaoQuestao> lista = avaliacaoQuestaoService.listByAvaliacao(id);

        int idx = -1;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdQuestao().equals(idQuestao)) {
                idx = i;
                break;
            }
        }

        // se não achou ou já é a primeira, não faz nada
        if (idx > 0) {
            Long idQuestaoAcima = lista.get(idx - 1).getIdQuestao();
            // troca a ordem entre a atual e a de cima
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

        List<AvaliacaoQuestao> lista = avaliacaoQuestaoService.listByAvaliacao(id);

        int idx = -1;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdQuestao().equals(idQuestao)) {
                idx = i;
                break;
            }
        }

        // se não achou ou já é a última, não faz nada
        if (idx != -1 && idx < lista.size() - 1) {
            Long idQuestaoAbaixo = lista.get(idx + 1).getIdQuestao();
            // troca a ordem entre a atual e a de baixo
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

        // remove o vínculo
        avaliacaoQuestaoService.deleteOne(id, idQuestao);
        // renumera as ordens para ficar 1,2,3,...
        avaliacaoQuestaoService.renumerarOrdem(id);

        redirect.addFlashAttribute("successMessage",
                "Questão removida da avaliação.");
        return "redirect:/app/avaliacoes/" + id + "/questoes";
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
        form.setEscopo("GLOBAL"); // default: todos os médicos

        model.addAttribute("pageTitle", "Nova avaliação");
        model.addAttribute("breadcrumb", "Avaliações");
        model.addAttribute("form", form);
        model.addAttribute("statusOptions", statusOptions());
        preencherCombos(model);

        return "gestor/avaliacoes-form";
    }


    // =========================================================
    // FORM - EDITAR
    // =========================================================
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Avaliacao a = avaliacaoService.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        AvaliacaoForm form = AvaliacaoForm.fromDomain(a);

        model.addAttribute("pageTitle", "Editar avaliação");
        model.addAttribute("breadcrumb", "Avaliações");
        model.addAttribute("form", form);
        model.addAttribute("statusOptions", statusOptions());
        preencherCombos(model);

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
            preencherCombos(model);
            return "gestor/avaliacoes-form";
        }

        AvaliacaoCreate cmd = new AvaliacaoCreate();
        cmd.setTitulo(form.getTitulo());
        cmd.setDataInicioAplic(form.getDataInicioAplic());
        cmd.setDataFimAplic(form.getDataFimAplic());
        cmd.setkMinimo(form.getkMinimo());
        cmd.setStatus(form.getStatus());
        cmd.setAtivo(form.getAtivo());
        // TODO: quando o domínio suportar esses campos, descomentar:
        // cmd.setEscopo(form.getEscopo());
        // cmd.setIdUnidade(form.getIdUnidade());
        // cmd.setIdEspecialidade(form.getIdEspecialidade());

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

        if (binding.hasErrors()) {
            model.addAttribute("pageTitle", "Editar avaliação");
            model.addAttribute("breadcrumb", "Avaliações");
            model.addAttribute("statusOptions", statusOptions());
            preencherCombos(model);
            return "gestor/avaliacoes-form";
        }

        AvaliacaoUpdate cmd = new AvaliacaoUpdate(
                form.getTitulo(),
                form.getDataInicioAplic(),
                form.getDataFimAplic(),
                form.getkMinimo(),
                form.getStatus(),
                form.getAtivo()
        );
        // TODO: quando AvaliacaoUpdate tiver esses campos, setar aqui:
        // cmd.setEscopo(form.getEscopo());
        // cmd.setIdUnidade(form.getIdUnidade());
        // cmd.setIdEspecialidade(form.getIdEspecialidade());

        try {
            avaliacaoService.update(id, cmd);
        } catch (AvaliacaoJaPublicadaException | AvaliacaoEncerradaException ex) {
            // mostra mensagem amigável e volta para a tela de edição
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/app/avaliacoes/" + id + "/editar";
        }

        redirect.addFlashAttribute("successMessage",
                "Avaliação atualizada com sucesso.");
        return "redirect:/app/avaliacoes";
    }



    // =========================================================
    // HELPER: opções de status
    // =========================================================
    private List<String> statusOptions() {
        return List.of("RASCUNHO", "PUBLICADA", "ENCERRADA");
    }

    // =========================================================
    // HELPER: opções de escopo de participantes
    // =========================================================
    private List<String> escopoOptions() {
        return List.of("GLOBAL", "UNIDADE", "ESPECIALIDADE");
    }

    // =========================================================
    // HELPER: combos usados no form (unidades / especialidades)
    // =========================================================
    private void preencherCombos(Model model) {
        var especialidadesCombo = especialidadeService.listAtivas();
        var unidadesCombo = unidadeService.list(
                new UnidadeFiltro(null, 0, 200, "nome", true, true)
        );

        model.addAttribute("especialidades", especialidadesCombo);
        model.addAttribute("unidades", unidadesCombo);
        model.addAttribute("escopoOptions", escopoOptions());
    }


    // =========================================================
    // VIEW MODEL do formulário
    // =========================================================
    public static class AvaliacaoForm {

        private Long id;

        @NotBlank
        private String titulo;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate dataInicioAplic;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate dataFimAplic;

        @NotNull
        @Min(2)
        private Integer kMinimo;

        @NotBlank
        private String status; // RASCUNHO | PUBLICADA | ENCERRADA

        private Boolean ativo = Boolean.TRUE;

        // >>> NOVOS CAMPOS <<<
        @NotBlank
        private String escopo;          // GLOBAL | UNIDADE | ESPECIALIDADE

        private Long idUnidade;         // se escopo = UNIDADE
        private Long idEspecialidade;   // se escopo = ESPECIALIDADE

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

        public String getEscopo() {
            return escopo;
        }

        public void setEscopo(String escopo) {
            this.escopo = escopo;
        }

        public Long getIdUnidade() {
            return idUnidade;
        }

        public void setIdUnidade(Long idUnidade) {
            this.idUnidade = idUnidade;
        }

        public Long getIdEspecialidade() {
            return idEspecialidade;
        }

        public void setIdEspecialidade(Long idEspecialidade) {
            this.idEspecialidade = idEspecialidade;
        }

        public static AvaliacaoForm fromDomain(Avaliacao a) {
            AvaliacaoForm f = new AvaliacaoForm();
            f.setId(a.getIdAvaliacao());
            f.setTitulo(a.getTitulo());
            f.setDataInicioAplic(a.getDataInicioAplic());
            f.setDataFimAplic(a.getDataFimAplic());
            f.setkMinimo(a.getkMinimo());
            f.setStatus(a.getStatus());
            f.setAtivo(a.getAtivo());

            // Ainda não temos escopo/unidade/especialidade no domínio,
            // então usamos defaults seguros:
            f.setEscopo("GLOBAL");
            f.setIdUnidade(null);
            f.setIdEspecialidade(null);

            return f;
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

        public Long getIdQuestao() {
            return idQuestao;
        }

        public void setIdQuestao(Long idQuestao) {
            this.idQuestao = idQuestao;
        }

        public Integer getOrdem() {
            return ordem;
        }

        public void setOrdem(Integer ordem) {
            this.ordem = ordem;
        }

        public String getEnunciado() {
            return enunciado;
        }

        public void setEnunciado(String enunciado) {
            this.enunciado = enunciado;
        }

        public Boolean getAtivaNaAval() {
            return ativaNaAval;
        }

        public void setAtivaNaAval(Boolean ativaNaAval) {
            this.ativaNaAval = ativaNaAval;
        }

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

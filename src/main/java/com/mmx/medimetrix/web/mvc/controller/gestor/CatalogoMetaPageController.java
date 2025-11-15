package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.application.criterio.service.CriterioService;
import com.mmx.medimetrix.application.meta.commands.MetaCreate;
import com.mmx.medimetrix.application.meta.commands.MetaUpdate;
import com.mmx.medimetrix.application.meta.queries.MetaFiltro;
import com.mmx.medimetrix.application.meta.service.MetaService;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;
import com.mmx.medimetrix.domain.core.Criterio;
import com.mmx.medimetrix.domain.core.Meta;
import com.mmx.medimetrix.domain.core.Especialidade;
import com.mmx.medimetrix.domain.core.Unidade;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/catalogo/metas")
public class CatalogoMetaPageController {

    private final MetaService metaService;
    private final CriterioService criterioService;
    private final UnidadeService unidadeService;
    private final EspecialidadeService especialidadeService;

    public CatalogoMetaPageController(MetaService metaService,
                                      CriterioService criterioService,
                                      UnidadeService unidadeService,
                                      EspecialidadeService especialidadeService) {
        this.metaService = metaService;
        this.criterioService = criterioService;
        this.unidadeService = unidadeService;
        this.especialidadeService = especialidadeService;
    }

    // ============ LISTA ============

    @GetMapping
    public String list(@RequestParam(name = "criterioId", required = false) Long criterioId,
                       @RequestParam(name = "escopo", required = false) String escopo,
                       @RequestParam(name = "status", required = false) String status,
                       Model model) {

        List<Criterio> criterios = criterioService
                .list(new CriterioFiltro(null, 0, 200));
        model.addAttribute("criterios", criterios);

        var filtro = new MetaFiltro(
                criterioId,
                null,
                null,
                null,
                0,
                200
        );

        List<Meta> metas = metaService.list(filtro);

        Map<Long, String> nomePorCriterio = criterios.stream()
                .collect(Collectors.toMap(
                        Criterio::getIdCriterio,
                        Criterio::getNome
                ));

        List<MetaListItemVM> itens = metas.stream()
                .filter(m -> filtrarPorEscopo(m, escopo))
                .filter(m -> filtrarPorStatus(m, status))
                .map(m -> MetaListItemVM.from(m, nomePorCriterio.get(m.getIdCriterio())))
                .toList();

        model.addAttribute("metas", itens);
        model.addAttribute("filtroCriterioId", criterioId);
        model.addAttribute("filtroEscopo", escopo);
        model.addAttribute("filtroStatus", status);

        model.addAttribute("pageTitle", "Catálogo • Metas");
        model.addAttribute("breadcrumb", "Catálogo");

        return "gestor/catalogo-metas-list";
    }

    private boolean filtrarPorEscopo(Meta m, String escopo) {
        if (escopo == null || escopo.isBlank()) return true;

        String codigo;
        if (m.getIdUnidade() != null && m.getIdEspecialidade() == null) {
            codigo = "UNIDADE";
        } else if (m.getIdEspecialidade() != null) {
            codigo = "ESPECIALIDADE";
        } else {
            codigo = "GLOBAL";
        }
        return escopo.equalsIgnoreCase(codigo);
    }

    private boolean filtrarPorStatus(Meta m, String status) {
        if (status == null || status.isBlank()) return true;
        boolean ativo = Boolean.TRUE.equals(m.getAtivo());
        if ("true".equalsIgnoreCase(status))  return ativo;
        if ("false".equalsIgnoreCase(status)) return !ativo;
        return true;
    }

    // ============ FORM HELPERS ============

    private void carregarCombos(Model model) {
        List<Criterio> criterios = criterioService.list(new CriterioFiltro(null, 0, 200));

        UnidadeFiltro filtroUn = new UnidadeFiltro(
                null,       // nomeLike
                0,          // page
                500,        // size
                "nome",     // sortBy
                true,       // asc
                true        // ativo (somente unidades ativas)
        );
        List<Unidade> unidades = unidadeService.list(filtroUn);

        List<Especialidade> especialidades = especialidadeService.listAtivas();

        model.addAttribute("criterios", criterios);
        model.addAttribute("unidades", unidades);
        model.addAttribute("especialidades", especialidades);
    }

    // Normaliza idUnidade/idEspecialidade de acordo com o escopo
    private void normalizarEscopo(MetaForm form) {
        String escopo = form.getEscopo();
        if (escopo == null || escopo.isBlank()) {
            escopo = "GLOBAL";
        }

        switch (escopo.toUpperCase()) {
            case "UNIDADE" -> {
                // meta por unidade: zera especialidade
                form.setIdEspecialidade(null);
            }
            case "ESPECIALIDADE" -> {
                // meta por especialidade: zera unidade
                form.setIdUnidade(null);
            }
            default -> {
                // GLOBAL: zera ambos
                form.setIdUnidade(null);
                form.setIdEspecialidade(null);
                form.setEscopo("GLOBAL");
            }
        }
    }

    // Deriva escopo a partir da meta (na edição)
    private String derivarEscopo(Meta m) {
        if (m.getIdUnidade() != null && m.getIdEspecialidade() == null) {
            return "UNIDADE";
        }
        if (m.getIdEspecialidade() != null) {
            return "ESPECIALIDADE";
        }
        return "GLOBAL";
    }

    // ============ NOVO ============

    @GetMapping("/novo")
    public String novo(Model model) {
        MetaForm form = new MetaForm();
        form.setAtivo(true);
        form.setOperador(">=");   // default
        form.setEscopo("GLOBAL"); // default

        model.addAttribute("metaForm", form);
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Nova Meta");
        model.addAttribute("breadcrumb", "Catálogo");

        carregarCombos(model);
        return "gestor/catalogo-metas-form";
    }

    // ============ EDITAR ============

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Meta> opt = metaService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/app/catalogo/metas";
        }
        Meta m = opt.get();

        MetaForm form = new MetaForm();
        form.setId(m.getIdMeta());
        form.setIdCriterio(m.getIdCriterio());
        form.setIdUnidade(m.getIdUnidade());
        form.setIdEspecialidade(m.getIdEspecialidade());
        form.setAlvo(m.getAlvo());
        form.setOperador(m.getOperador());
        form.setVigenciaInicio(m.getVigenciaInicio());
        form.setVigenciaFim(m.getVigenciaFim());
        form.setPrioridade(m.getPrioridade());
        form.setAtivo(m.getAtivo());
        form.setJustificativa(m.getJustificativa());
        form.setEscopo(derivarEscopo(m));   // <<< NOVO

        model.addAttribute("metaForm", form);
        model.addAttribute("isEdit", true);
        model.addAttribute("pageTitle", "Editar Meta");
        model.addAttribute("breadcrumb", "Catálogo");

        carregarCombos(model);
        return "gestor/catalogo-metas-form";
    }

    // ============ SALVAR (CREATE/UPDATE) ============

    @PostMapping
    public String criar(@ModelAttribute("metaForm") MetaForm form) {
        normalizarEscopo(form);

        metaService.create(new MetaCreate(
                form.getIdCriterio(),
                form.getIdUnidade(),
                form.getIdEspecialidade(),
                form.getAlvo(),
                form.getOperador(),
                form.getVigenciaInicio(),
                form.getVigenciaFim(),
                form.getPrioridade(),
                form.getJustificativa()
        ));
        return "redirect:/app/catalogo/metas";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("metaForm") MetaForm form) {
        normalizarEscopo(form);

        metaService.update(id, new MetaUpdate(
                form.getIdCriterio(),
                form.getIdUnidade(),
                form.getIdEspecialidade(),
                form.getAlvo(),
                form.getOperador(),
                form.getVigenciaInicio(),
                form.getVigenciaFim(),
                form.getAtivo(),
                form.getPrioridade(),
                form.getJustificativa()
        ));
        return "redirect:/app/catalogo/metas";
    }
}

package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.application.criterio.service.CriterioService;
import com.mmx.medimetrix.application.questao.commands.QuestaoCreate;
import com.mmx.medimetrix.application.questao.commands.QuestaoUpdate;
import com.mmx.medimetrix.application.questao.queries.QuestaoFiltro;
import com.mmx.medimetrix.application.questao.service.QuestaoService;
import com.mmx.medimetrix.domain.core.Criterio;
import com.mmx.medimetrix.domain.core.Questao;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/catalogo/questoes")
public class CatalogoQuestaoPageController {

    private final QuestaoService questaoService;
    private final CriterioService criterioService;

    public CatalogoQuestaoPageController(QuestaoService questaoService,
                                         CriterioService criterioService) {
        this.questaoService = questaoService;
        this.criterioService = criterioService;
    }

    // ============ LISTA ============

    @GetMapping
    public String list(@RequestParam(name = "criterioId", required = false) Long criterioId,
                       @RequestParam(name = "sensivel",   required = false) String sensivelParam,
                       @RequestParam(name = "visivel",    required = false) String visivelParam,
                       @RequestParam(name = "termo",      required = false) String termo,
                       @RequestParam(name = "page",       defaultValue = "0") Integer page,
                       @RequestParam(name = "size",       defaultValue = "20") Integer size,
                       Model model) {

        model.addAttribute("pageTitle", "Catálogo • Questões");
        model.addAttribute("breadcrumb", "Catálogo");

        // Critérios para filtro e para mapear nomes
        List<Criterio> criterios = criterioService
                .list(new CriterioFiltro(null, 0, 200));
        model.addAttribute("criterios", criterios);

        Map<Long, String> criterioNomePorId = criterios.stream()
                .collect(Collectors.toMap(
                        Criterio::getIdCriterio,
                        Criterio::getNome
                ));

        // Filtro de backend: critério + termo (sem tipo)
        var filtro = new QuestaoFiltro(
                termo,
                null,          // tipo não filtramos mais aqui
                criterioId,
                page,
                size
        );

        List<Questao> questoesDomain = questaoService.list(filtro);
        boolean hasNext = questoesDomain.size() == size;  // paginação básica (antes do filtro em memória)

        // Converte parâmetros "true"/"false" em Boolean
        Boolean filtroSensivel =
                "true".equalsIgnoreCase(sensivelParam) ? Boolean.TRUE :
                        ("false".equalsIgnoreCase(sensivelParam) ? Boolean.FALSE : null);

        Boolean filtroVisivel =
                "true".equalsIgnoreCase(visivelParam) ? Boolean.TRUE :
                        ("false".equalsIgnoreCase(visivelParam) ? Boolean.FALSE : null);

        // Filtros de sensibilidade/visibilidade em memória (na página retornada)
        if (filtroSensivel != null) {
            questoesDomain = questoesDomain.stream()
                    .filter(q -> Boolean.TRUE.equals(q.getSensivel()) == filtroSensivel)
                    .toList();
        }
        if (filtroVisivel != null) {
            questoesDomain = questoesDomain.stream()
                    .filter(q -> Boolean.TRUE.equals(q.getVisivelParaGestor()) == filtroVisivel)
                    .toList();
        }

        List<QuestaoListItemVM> questoes = questoesDomain.stream()
                .map(q -> {
                    String critNome = q.getIdCriterio() != null
                            ? criterioNomePorId.getOrDefault(q.getIdCriterio(), "(sem critério)")
                            : "(sem critério)";
                    return QuestaoListItemVM.from(q, critNome);
                })
                .toList();


        // lista + paginação
        model.addAttribute("questoes", questoes);
        model.addAttribute("pageIdx", page);
        model.addAttribute("size", size);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", hasNext);

        // devolve filtros selecionados
        model.addAttribute("filtroCriterioId", criterioId);
        model.addAttribute("filtroTermo", termo);
        model.addAttribute("filtroSensivel", sensivelParam);
        model.addAttribute("filtroVisivel", visivelParam);

        return "gestor/catalogo-questoes-list";
    }


    // ============ FORM NOVO ============

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Questão");
        model.addAttribute("breadcrumb", "Catálogo");

        QuestaoForm form = new QuestaoForm();
        form.setAtivo(true);
        form.setSensivel(false);
        form.setVisivelParaGestor(true);
        form.setObrigatoriedade("OBRIGATORIA");
        form.setTipo("LIKERT_5");

        prepararForm(model, form, false);
        return "gestor/catalogo-questoes-form";
    }

    // ============ FORM EDITAR ============

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Questão");
        model.addAttribute("breadcrumb", "Catálogo");

        Questao q = questaoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestaoForm form = new QuestaoForm();
        form.setId(q.getIdQuestao());
        form.setIdCriterio(q.getIdCriterio());
        form.setEnunciado(q.getEnunciado());
        form.setTipo(q.getTipo());
        form.setObrigatoriedade(q.getObrigatoriedade());
        form.setValidacaoNumMin(q.getValidacaoNumMin());
        form.setValidacaoNumMax(q.getValidacaoNumMax());
        form.setTamanhoTextoMax(q.getTamanhoTextoMax());
        form.setSensivel(q.getSensivel());
        form.setVisivelParaGestor(q.getVisivelParaGestor());
        form.setAtivo(q.getAtivo());
        form.setOrdemSugerida(q.getOrdemSugerida());

        prepararForm(model, form, true);
        return "gestor/catalogo-questoes-form";
    }

    // ============ CREATE ============

    @PostMapping
    public String criar(@Valid @ModelAttribute("questaoForm") QuestaoForm form,
                        BindingResult bindingResult,
                        Model model) {

        if (form.getIdCriterio() == null || form.getEnunciado() == null || form.getEnunciado().isBlank()) {
            bindingResult.reject("invalid", "Critério e enunciado são obrigatórios.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Nova Questão");
            model.addAttribute("breadcrumb", "Catálogo");
            prepararForm(model, form, false);
            return "gestor/catalogo-questoes-form";
        }

        questaoService.create(new QuestaoCreate(
                form.getIdCriterio(),
                form.getEnunciado(),
                form.getTipo(),
                form.getObrigatoriedade(),
                form.getValidacaoNumMin(),
                form.getValidacaoNumMax(),
                form.getTamanhoTextoMax(),
                form.getSensivel(),
                form.getVisivelParaGestor(),
                form.getOrdemSugerida()
        ));

        return "redirect:/app/catalogo/questoes";
    }

    // ============ UPDATE ============

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("questaoForm") QuestaoForm form,
                            BindingResult bindingResult,
                            Model model) {

        if (form.getIdCriterio() == null || form.getEnunciado() == null || form.getEnunciado().isBlank()) {
            bindingResult.reject("invalid", "Critério e enunciado são obrigatórios.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Questão");
            model.addAttribute("breadcrumb", "Catálogo");
            prepararForm(model, form, true);
            return "gestor/catalogo-questoes-form";
        }

        // Atualiza parte "catalogo": os campos do form + ativo
        questaoService.update(id, new QuestaoUpdate(
                form.getEnunciado(),
                form.getTipo(),
                form.getObrigatoriedade(),
                form.getValidacaoNumMin(),
                form.getValidacaoNumMax(),
                form.getTamanhoTextoMax(),
                form.getSensivel(),
                form.getVisivelParaGestor(),
                form.getAtivo(),
                form.getOrdemSugerida()
        ));

        return "redirect:/app/catalogo/questoes";
    }

    // ============ ATIVAR / DESATIVAR ============

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {

        boolean excluidaFisicamente = questaoService.deactivate(id);

        if (excluidaFisicamente) {
            redirectAttributes.addFlashAttribute(
                    "flash_success",
                    "Questão excluída com sucesso do catálogo."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "flash_error",
                    "Esta questão já foi utilizada em avaliações e, por segurança, foi apenas desativada no catálogo."
            );
        }

        return "redirect:/app/catalogo/questoes";
    }



    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id) {
        questaoService.activate(id);
        return "redirect:/app/catalogo/questoes";
    }

    // ============ HELPERS ============

    private void prepararForm(Model model, QuestaoForm form, boolean isEdit) {
        List<Criterio> criterios = criterioService
                .list(new CriterioFiltro(null, 0, 200));
        model.addAttribute("criterios", criterios);
        model.addAttribute("questaoForm", form);
        model.addAttribute("isEdit", isEdit);
    }
}

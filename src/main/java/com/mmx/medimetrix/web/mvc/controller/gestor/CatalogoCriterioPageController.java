package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.application.criterio.commands.CriterioCreate;
import com.mmx.medimetrix.application.criterio.commands.CriterioUpdate;
import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.application.criterio.service.CriterioService;
import com.mmx.medimetrix.domain.core.Criterio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/app/catalogo/criterios")
public class CatalogoCriterioPageController {

    private final CriterioService service;

    public CatalogoCriterioPageController(CriterioService service) {
        this.service = service;
    }

    // LISTA
    @GetMapping
    public String list(@RequestParam(required = false) String termo, Model model) {
        model.addAttribute("pageTitle", "Catálogo • Critérios");
        model.addAttribute("breadcrumb", "Catálogo");

        var filtro = new CriterioFiltro(termo, 0, 100);
        List<Criterio> criterios = service.list(filtro);

        model.addAttribute("criterios", criterios);
        model.addAttribute("termo", termo);

        return "gestor/catalogo-criterios-list";
    }

    // ==== FORM NOVO ====
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Novo Critério");
        model.addAttribute("breadcrumb", "Catálogo");

        CriterioForm form = new CriterioForm();
        int maxOrdem = service.getMaxOrdem();
        form.setOrdemSugerida(maxOrdem + 1); // só informativo

        model.addAttribute("form", form);
        model.addAttribute("isEdit", false);

        return "gestor/catalogo-criterios-form";
    }



    // ==== FORM EDITAR ====
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Critério");
        model.addAttribute("breadcrumb", "Catálogo");

        Criterio c = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Critério não encontrado"));

        CriterioForm form = new CriterioForm();
        form.setId(c.getIdCriterio());
        form.setNome(c.getNome());
        form.setDefinicaoOperacional(c.getDefinicaoOperacional());
        form.setDescricao(c.getDescricao());
        form.setPeso(c.getPeso());
        form.setOrdemSugerida(c.getOrdemSugerida());
        form.setAtivo(c.getAtivo());
        form.setVersao(c.getVersao());

        int maxOrdem = service.getMaxOrdem();
        model.addAttribute("maxOrdem", maxOrdem == 0 ? 1 : maxOrdem);

        model.addAttribute("form", form);
        model.addAttribute("isEdit", true);
        model.addAttribute("id", id);

        return "gestor/catalogo-criterios-form";
    }

    // ==== CREATE ====
    @PostMapping
    public String criar(@Valid @ModelAttribute("form") CriterioForm form,
                        BindingResult bindingResult,
                        Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Novo Critério");
            model.addAttribute("breadcrumb", "Catálogo");
            model.addAttribute("isEdit", false);
            return "gestor/catalogo-criterios-form";
        }

        service.create(new CriterioCreate(
                form.getNome(),
                form.getDefinicaoOperacional(),
                form.getDescricao(),
                form.getPeso(),
                null,           // ordemSugerida -> sempre calculada no service
                form.getAtivo()
        ));

        return "redirect:/app/catalogo/criterios";
    }

    // ==== UPDATE ====
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") CriterioForm form,
                            BindingResult bindingResult,
                            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Critério");
            model.addAttribute("breadcrumb", "Catálogo");
            model.addAttribute("isEdit", true);
            model.addAttribute("id", id);
            return "gestor/catalogo-criterios-form";
        }

        service.update(id, new CriterioUpdate(
                form.getNome(),
                form.getDefinicaoOperacional(),
                form.getDescricao(),
                form.getPeso(),
                null,           // ordemSugerida -> sempre calculada no service
                form.getAtivo()
        ));

        return "redirect:/app/catalogo/criterios";
    }

    @PostMapping("/{id}/up")
    public String moverParaCima(@PathVariable Long id) {
        service.moveUp(id);
        return "redirect:/app/catalogo/criterios";
    }

    @PostMapping("/{id}/down")
    public String moverParaBaixo(@PathVariable Long id) {
        service.moveDown(id);
        return "redirect:/app/catalogo/criterios";
    }


    // ==== DESATIVAR (Excluir lógico) ====
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        service.deactivate(id);
        return "redirect:/app/catalogo/criterios";
    }

    // (opcional) reativar, se quiser
    @PostMapping("/{id}/ativar")
    public String reativar(@PathVariable Long id) {
        service.activate(id);
        return "redirect:/app/catalogo/criterios";
    }
}

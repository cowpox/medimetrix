package com.mmx.medimetrix.web.mvc.controller.gestor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/catalogo/criterios")
public class CatalogoCriterioPageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Catálogo • Critérios");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-criterios-list";
    }

    // ==== FORMS (stubs) ====

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Novo Critério");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-criterios-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Critério");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-criterios-form";
    }

    @PostMapping
    public String criar() {
        // TODO ligar service depois
        return "redirect:/app/catalogo/criterios";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id) {
        // TODO ligar service depois
        return "redirect:/app/catalogo/criterios";
    }
}
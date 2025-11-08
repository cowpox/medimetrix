package com.mmx.medimetrix.web.mvc.controller.gestor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/catalogo/questoes")
public class CatalogoQuestaoPageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Catálogo • Questões");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-questoes-list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Questão");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-questoes-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Questão");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-questoes-form";
    }

    @PostMapping
    public String criar() { return "redirect:/app/catalogo/questoes"; }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id) { return "redirect:/app/catalogo/questoes"; }
}

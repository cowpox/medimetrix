package com.mmx.medimetrix.web.mvc.controller.gestor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/catalogo/metas")
public class CatalogoMetaPageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Catálogo • Metas");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-metas-list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Meta");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-metas-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Meta");
        model.addAttribute("breadcrumb", "Catálogo");
        return "gestor/catalogo-metas-form";
    }

    @PostMapping
    public String criar() { return "redirect:/app/catalogo/metas"; }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id) { return "redirect:/app/catalogo/metas"; }
}

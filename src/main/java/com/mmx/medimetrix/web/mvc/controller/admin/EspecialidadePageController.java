package com.mmx.medimetrix.web.mvc.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/admin/especialidades")
public class EspecialidadePageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Especialidades");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/especialidades-list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Especialidade");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/especialidades-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Especialidade");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/especialidades-form";
    }

    @PostMapping
    public String criar() { return "redirect:/app/admin/especialidades"; }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id) { return "redirect:/app/admin/especialidades"; }
}


package com.mmx.medimetrix.web.mvc.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/admin/medicos")
public class MedicoPageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Médicos");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/medicos-list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Novo Médico");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/medicos-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Médico");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/medicos-form";
    }

    @PostMapping
    public String criar() { return "redirect:/app/admin/medicos"; }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id) { return "redirect:/app/admin/medicos"; }
}


package com.mmx.medimetrix.web.mvc.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/admin/usuarios")
public class UsuarioPageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Usuários");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/usuarios-list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Novo Usuário");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/usuarios-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Usuário");
        model.addAttribute("breadcrumb", "Admin");
        return "admin/usuarios-form";
    }

    @PostMapping
    public String criar() { return "redirect:/app/admin/usuarios"; }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id) { return "redirect:/app/admin/usuarios"; }
}

package com.mmx.medimetrix.web.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/app")
    public String home(@RequestParam(name = "role", required = false) String role, Model model) {
        if ("MEDICO".equalsIgnoreCase(role)) return "redirect:/app/coleta";
        if ("GESTOR".equalsIgnoreCase(role)) return "redirect:/app/avaliacoes";
        if ("ADMIN".equalsIgnoreCase(role)) return "redirect:/app/admin/usuarios";
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("breadcrumb", "Home");
        return "home/dashboard";
    }
}
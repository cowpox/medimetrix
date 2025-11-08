package com.mmx.medimetrix.web.mvc.controller.gestor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app/avaliacoes")
public class AvaliacoesPageController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Avaliações");
        model.addAttribute("breadcrumb", "Avaliações");
        return "gestor/avaliacoes-list";
    }
}
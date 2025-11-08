package com.mmx.medimetrix.web.mvc.controller.gestor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/app/avaliacoes")
public class AvaliacaoWizardPageController {

    // Passo 1 — criar rascunho (metadados)
    @GetMapping("/nova")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Avaliação • Passo 1");
        model.addAttribute("breadcrumb", "Avaliação");
        return "gestor/avaliacao-wizard/passo1-metadados";
    }

    // Editar rascunho existente: navegar entre passos (?passo=1..5)
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         @RequestParam(defaultValue = "1") int passo,
                         Model model) {
        model.addAttribute("pageTitle", "Avaliação • Passo " + passo);
        model.addAttribute("breadcrumb", "Avaliação");
        return switch (passo) {
            case 1 -> "gestor/avaliacao-wizard/passo1-metadados";
            case 2 -> "gestor/avaliacao-wizard/passo2-questoes";
            case 3 -> "gestor/avaliacao-wizard/passo3-participantes";
            case 4 -> "gestor/avaliacao-wizard/passo4-sigilo";
            case 5 -> "gestor/avaliacao-wizard/passo5-publicar";
            default -> "gestor/avaliacao-wizard/passo1-metadados";
        };
    }
}

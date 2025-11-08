package com.mmx.medimetrix.web.mvc.controller.medico;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/app/coleta")
public class ColetaPageController {

    // Lista "Minhas Avaliações"
    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Minhas Avaliações");
        model.addAttribute("breadcrumb", "Coleta");
        return "medico/coleta-list";
    }

    // Detalhe de uma avaliação
    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Avaliação");
        model.addAttribute("breadcrumb", "Coleta");
        return "medico/coleta-detalhe";
    }

    // Formulário para responder
    @GetMapping("/{id}/responder")
    public String responder(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Responder");
        model.addAttribute("breadcrumb", "Coleta");
        return "medico/coleta-form";
    }

    // Salvar rascunho (simulado)
    @PostMapping("/{id}/responder")
    public String salvarRascunho(@PathVariable Long id) {
        // TODO: opcional - adicionar RedirectAttributes com flash_success
        return "redirect:/app/coleta/" + id + "/responder";
    }

    // Enviar respostas (simulado) -> comprovante
    @PostMapping("/{id}/enviar")
    public String enviar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Comprovante");
        model.addAttribute("breadcrumb", "Coleta");
        return "medico/coleta-comprovante";
    }
}

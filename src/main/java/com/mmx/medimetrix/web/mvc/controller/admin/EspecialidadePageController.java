package com.mmx.medimetrix.web.mvc.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app/admin/especialidades")
public class EspecialidadePageController {

    // VM simples só para “desenhar” a tela (front-only)
    public record EspecialidadeVM(Long id, String nome, Boolean ativo) {}

    // LISTA (com filtro de status + paginação simples – mock)
    @GetMapping
    public String list(@RequestParam(required = false) String termo,
                       @RequestParam(required = false) Boolean ativo, // null=todos, true/false
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {

        // mock data (trocaremos pelo service quando ligar ao back)
        var all = new ArrayList<EspecialidadeVM>(List.of(
                new EspecialidadeVM(1L, "Clínica Médica", true),
                new EspecialidadeVM(2L, "Cardiologia", true),
                new EspecialidadeVM(3L, "Dermatologia", false)
        ));

        // filtro (front-only)
        var itens = all.stream()
                .filter(e -> ativo == null || e.ativo.equals(ativo))
                .filter(e -> termo == null || termo.isBlank()
                        || e.nome.toLowerCase().contains(termo.toLowerCase()))
                .toList();

        // paginação besta (mock)
        int from = Math.max(0, page * size);
        int to   = Math.min(itens.size(), from + size);
        var pageItems = from < to ? itens.subList(from, to) : List.<EspecialidadeVM>of();
        boolean hasPrev = page > 0;
        boolean hasNext = to < itens.size();

        model.addAttribute("pageTitle", "Especialidades");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("itens", pageItems);

        // mantém estado dos filtros/paginação
        model.addAttribute("termo", termo);
        model.addAttribute("ativo", ativo == null ? "" : ativo.toString());
        model.addAttribute("pageIdx", page);
        model.addAttribute("size", size);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);

        return "admin/especialidades-list";
    }

    // NOVO (form somente com Nome + Ativo)
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Especialidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", false);
        return "admin/especialidades-form";
    }

    // EDITAR (mock)
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Especialidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", true);
        model.addAttribute("id", id);
        return "admin/especialidades-form";
    }

    // STUBs (mantém navegação)
    @PostMapping public String criar() { return "redirect:/app/admin/especialidades"; }
    @PostMapping("/{id}") public String atualizar(@PathVariable Long id) { return "redirect:/app/admin/especialidades"; }
    @PostMapping("/{id}/ativar") public String ativar(@PathVariable Long id){ return "redirect:/app/admin/especialidades"; }
    @PostMapping("/{id}/desativar") public String desativar(@PathVariable Long id){ return "redirect:/app/admin/especialidades"; }
    @PostMapping("/{id}/excluir") public String excluir(@PathVariable Long id){ return "redirect:/app/admin/especialidades"; }
}

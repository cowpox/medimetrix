package com.mmx.medimetrix.web.mvc.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app/admin/unidades")
public class UnidadePageController {

    // VM só para a camada de view (mock)
    public record UnidadeVM(Long id, String nome, String gestorNome, Boolean ativo) {}

    @GetMapping
    public String list(@RequestParam(required = false) String termo,
                       @RequestParam(required = false) Boolean ativo,  // null=todos
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {

        var all = new ArrayList<UnidadeVM>(List.of(
                new UnidadeVM(1L, "PA Centro", "Alice Carvalho", true),
                new UnidadeVM(2L, "PA Norte", "Bruno Dias", true),
                new UnidadeVM(3L, "Clínica Sul", "Carla Menezes", false)
        ));

        var filtrado = all.stream()
                .filter(u -> ativo == null || u.ativo.equals(ativo))
                .filter(u -> termo == null || termo.isBlank() ||
                        u.nome.toLowerCase().contains(termo.toLowerCase()) ||
                        (u.gestorNome != null && u.gestorNome.toLowerCase().contains(termo.toLowerCase())))
                .toList();

        int from = Math.max(0, page * size);
        int to   = Math.min(filtrado.size(), from + size);
        var itens = from < to ? filtrado.subList(from, to) : List.<UnidadeVM>of();

        model.addAttribute("pageTitle", "Unidades");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("itens", itens);

        model.addAttribute("termo", termo);
        model.addAttribute("ativo", ativo == null ? "" : ativo.toString());
        model.addAttribute("pageIdx", page);
        model.addAttribute("size", size);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", to < filtrado.size());

        return "admin/unidades-list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Unidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", false);
        return "admin/unidades-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Unidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", true);
        model.addAttribute("id", id);
        return "admin/unidades-form";
    }

    // stubs para navegação
    @PostMapping public String criar(){ return "redirect:/app/admin/unidades"; }
    @PostMapping("/{id}") public String atualizar(@PathVariable Long id){ return "redirect:/app/admin/unidades"; }
    @PostMapping("/{id}/ativar") public String ativar(@PathVariable Long id){ return "redirect:/app/admin/unidades"; }
    @PostMapping("/{id}/desativar") public String desativar(@PathVariable Long id){ return "redirect:/app/admin/unidades"; }
    @PostMapping("/{id}/excluir") public String excluir(@PathVariable Long id){ return "redirect:/app/admin/unidades"; }
}

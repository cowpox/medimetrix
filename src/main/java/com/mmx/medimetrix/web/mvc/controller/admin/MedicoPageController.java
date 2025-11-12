package com.mmx.medimetrix.web.mvc.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app/admin/medicos")
public class MedicoPageController {

    // View Model só para a tela (mock)
    public record MedicoVM(
            Long id, String nome,
            String crmNumero, String crmUf,
            Long especialidadeId, String especialidadeNome,
            Long unidadeId, String unidadeNome,
            Boolean ativo
    ) {}

    // opções de filtros (mock)
    private record Opt(Long id, String nome) {}
    private static final List<Opt> ESPECIALIDADES = List.of(
            new Opt(1L, "Clínica Médica"),
            new Opt(2L, "Cardiologia"),
            new Opt(3L, "Pediatria")
    );
    private static final List<Opt> UNIDADES = List.of(
            new Opt(10L, "PA Centro"),
            new Opt(11L, "PA Norte"),
            new Opt(12L, "Clínica Sul")
    );

    @GetMapping
    public String list(@RequestParam(required = false) String termo,          // nome ou CRM
                       @RequestParam(required = false) Long especialidadeId,
                       @RequestParam(required = false) Long unidadeId,
                       @RequestParam(required = false) Boolean ativo,         // null=todos
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {

        // dataset fake
        var all = new ArrayList<MedicoVM>(List.of(
                new MedicoVM(1L,"Dr. João Silva","12345","PR",1L,"Clínica Médica",10L,"PA Centro", true),
                new MedicoVM(2L,"Dra. Maria Souza","88776","SP",2L,"Cardiologia",   11L,"PA Norte",  true),
                new MedicoVM(3L,"Dr. Pedro Lima","44551","PR",3L,"Pediatria",      12L,"Clínica Sul", false)
        ));

        var filtrado = all.stream()
                .filter(m -> especialidadeId == null || m.especialidadeId.equals(especialidadeId))
                .filter(m -> unidadeId == null || m.unidadeId.equals(unidadeId))
                .filter(m -> ativo == null || m.ativo.equals(ativo))
                .filter(m -> {
                    if (termo == null || termo.isBlank()) return true;
                    var t = termo.toLowerCase();
                    return m.nome.toLowerCase().contains(t)
                            || m.crmNumero.toLowerCase().contains(t);
                })
                .toList();

        int limit = Math.max(1, size);
        int from = Math.max(0, page) * limit;
        int to   = Math.min(filtrado.size(), from + limit);
        var itens = from < to ? filtrado.subList(from, to) : List.<MedicoVM>of();

        model.addAttribute("pageTitle", "Médicos");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("itens", itens);

        // filtros + estado
        model.addAttribute("termo", termo);
        model.addAttribute("especialidadeId", especialidadeId);
        model.addAttribute("unidadeId", unidadeId);
        model.addAttribute("ativo", ativo == null ? "" : ativo.toString());
        model.addAttribute("especialidades", ESPECIALIDADES);
        model.addAttribute("unidades", UNIDADES);

        // paginação
        model.addAttribute("pageIdx", page);
        model.addAttribute("size", limit);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", to < filtrado.size());

        return "admin/medicos-list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Novo Médico");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", false);
        model.addAttribute("especialidades", ESPECIALIDADES);
        model.addAttribute("unidades", UNIDADES);
        return "admin/medicos-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Médico");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", true);
        model.addAttribute("id", id);
        model.addAttribute("especialidades", ESPECIALIDADES);
        model.addAttribute("unidades", UNIDADES);
        return "admin/medicos-form";
    }

    // stubs navegação
    @PostMapping public String criar(){ return "redirect:/app/admin/medicos"; }
    @PostMapping("/{id}") public String atualizar(@PathVariable Long id){ return "redirect:/app/admin/medicos"; }
    @PostMapping("/{id}/excluir") public String excluir(@PathVariable Long id){ return "redirect:/app/admin/medicos"; }
}

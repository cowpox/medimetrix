package com.mmx.medimetrix.web.mvc.controller.admin;

import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeCreate;
import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeUpdate;
import com.mmx.medimetrix.application.especialidade.queries.EspecialidadeFiltro;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.domain.core.Especialidade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/app/admin/especialidades")
public class EspecialidadePageController {

    private final EspecialidadeService service;

    public EspecialidadePageController(EspecialidadeService service) {
        this.service = service;
    }

    // VM simples para a tabela
    public record EspecialidadeVM(Long id, String nome, Boolean ativo) {}

    @GetMapping
    public String list(@RequestParam(required = false) String termo,
                       @RequestParam(required = false) Boolean ativo, // null=todos
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(defaultValue = "NOME") String sort,   // <= NOVO (como em Usuários)
                       @RequestParam(defaultValue = "true") Boolean asc,   // <= NOVO
                       Model model) {

        int querySize = Math.max(1, size) + 1;

        // mapa da view ("ID" | "NOME") -> chave do filtro ("id" | "nome")
        String sortBy = "nome";
        if ("ID".equalsIgnoreCase(sort)) sortBy = "id";

        var filtro = new EspecialidadeFiltro(
                (StringUtils.hasText(termo) ? termo.trim() : null),
                page,
                querySize,
                sortBy,
                asc,
                ativo
        );

        List<Especialidade> lista = service.list(filtro);

        boolean hasNext = lista.size() > size;
        if (hasNext) lista = lista.subList(0, size);
        boolean hasPrev = page > 0;

        var vms = lista.stream()
                .map(e -> new EspecialidadeVM(
                        e.getIdEspecialidade(),
                        e.getNome(),
                        Boolean.TRUE.equals(e.getAtivo())
                ))
                .toList();

        model.addAttribute("pageTitle", "Especialidades");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("itens", vms);

        model.addAttribute("termo", termo);
        model.addAttribute("ativo", ativo == null ? "" : ativo.toString());
        model.addAttribute("pageIdx", page);
        model.addAttribute("size", size);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);

        // estes dois são usados pelo fragment para pintar setas/links
        model.addAttribute("sort", sort); // "ID" ou "NOME"
        model.addAttribute("asc", asc);

        return "admin/especialidades-list";
    }


    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Especialidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", false);
        return "admin/especialidades-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        var opt = service.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Especialidade não encontrada.");
            return "redirect:/app/admin/especialidades";
        }
        var e = opt.get();
        model.addAttribute("pageTitle", "Editar Especialidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", true);
        model.addAttribute("id", e.getIdEspecialidade());
        model.addAttribute("nome", e.getNome());
        model.addAttribute("ativoChecked", Boolean.TRUE.equals(e.getAtivo()));
        return "admin/especialidades-form";
    }

    @PostMapping
    public String criar(@RequestParam String nome,
                        @RequestParam(required = false) String ativo,
                        RedirectAttributes ra) {
        boolean ativoBool = (ativo != null);
        var created = service.create(new EspecialidadeCreate(nome != null ? nome.trim() : ""));
        if (!ativoBool) service.deactivate(created.getIdEspecialidade());
        ra.addFlashAttribute("flashSuccess", "Especialidade criada com sucesso.");
        return "redirect:/app/admin/especialidades";
    }


    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @RequestParam(required = false) String nome,
                            @RequestParam(required = false) String ativo,
                            RedirectAttributes ra) {
        Boolean ativoBool = (ativo != null);
        var cmd = new EspecialidadeUpdate(
                org.springframework.util.StringUtils.hasText(nome) ? nome.trim() : null,
                ativoBool
        );
        service.update(id, cmd);
        ra.addFlashAttribute("flashSuccess", "Especialidade atualizada com sucesso.");
        return "redirect:/app/admin/especialidades";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, RedirectAttributes ra) {
        service.activate(id);
        ra.addFlashAttribute("flashSuccess", "Especialidade ativada.");
        return "redirect:/app/admin/especialidades";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes ra) {
        service.deactivate(id);
        ra.addFlashAttribute("flashSuccess", "Especialidade desativada.");
        return "redirect:/app/admin/especialidades";
    }

    // Removido: excluir/hard delete neste ciclo
}

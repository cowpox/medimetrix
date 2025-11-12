package com.mmx.medimetrix.web.mvc.controller.admin;

import com.mmx.medimetrix.application.unidade.commands.UnidadeCreate;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.domain.core.Unidade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/app/admin/unidades")
public class UnidadePageController {

    private final UnidadeService unidadeService;
    private final UsuarioService usuarioService;

    public UnidadePageController(UnidadeService unidadeService, UsuarioService usuarioService) {
        this.unidadeService = unidadeService;
        this.usuarioService = usuarioService;
    }

    // VM só para a camada de view
    public record UnidadeVM(Long id, String nome, String gestorNome, Boolean ativo) {}

    @GetMapping
    public String list(@RequestParam(required = false) String termo,
                       @RequestParam(required = false) Boolean ativo,  // null=todos
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(defaultValue = "NOME") String sort,    // "ID" | "NOME" | "GESTOR"
                       @RequestParam(defaultValue = "true") Boolean asc,
                       Model model) {

        // size+1 para detectar hasNext
        int querySize = Math.max(1, size) + 1;

        // mapa da view -> chave do filtro
        String sortBy = "nome";
        if ("ID".equalsIgnoreCase(sort)) sortBy = "id";
        else if ("GESTOR".equalsIgnoreCase(sort)) sortBy = "gestor"; // ordena por GESTOR_USUARIO_ID

        var filtro = new UnidadeFiltro(
                (StringUtils.hasText(termo) ? termo.trim() : null),
                page,
                querySize,
                sortBy,
                asc,
                ativo
        );

        List<Unidade> lista = unidadeService.list(filtro);

        boolean hasNext = lista.size() > size;
        if (hasNext) lista = lista.subList(0, size);
        boolean hasPrev = page > 0;

        var vms = lista.stream()
                .map(u -> {
                    String gestorNome = null;
                    try {
                        var gestor = usuarioService.getById(u.getGestorUsuarioId());
                        gestorNome = gestor != null ? gestor.getNome() : null;
                    } catch (Exception ignored) { /* se não achar, deixa nulo */ }
                    return new UnidadeVM(
                            u.getIdUnidade(),
                            u.getNome(),
                            gestorNome,
                            Boolean.TRUE.equals(u.getAtivo())
                    );
                })
                .toList();

        model.addAttribute("pageTitle", "Unidades");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("itens", vms);

        model.addAttribute("termo", termo);
        model.addAttribute("ativo", ativo == null ? "" : ativo.toString());
        model.addAttribute("pageIdx", page);
        model.addAttribute("size", size);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);

        // usados pelo fragment (setas)
        model.addAttribute("sort", sort); // "ID" | "NOME" | "GESTOR"
        model.addAttribute("asc",  asc);

        return "admin/unidades-list";
    }

    // ====== STUBs (manter navegação; sem hard delete neste ciclo) ======
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pageTitle", "Nova Unidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", false);

        // gestores ativos
        var gestores = usuarioService.listByPapelAtivo(com.mmx.medimetrix.domain.enums.Papel.GESTOR, true)
                .stream()
                .map(u -> new Object[]{ u.getIdUsuario(), u.getNome() })
                .toList();
        model.addAttribute("gestores", gestores);
        return "admin/unidades-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

        var opt = unidadeService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("flashError", "Unidade não encontrada.");
            return "redirect:/app/admin/unidades";
        }
        var u = opt.get();

        // título/breadcrumb/flags
        model.addAttribute("pageTitle", "Editar Unidade");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", true);
        model.addAttribute("id", u.getIdUnidade());

        // valores atuais
        model.addAttribute("nome", u.getNome());
        model.addAttribute("gestorId", u.getGestorUsuarioId());
        model.addAttribute("ativoChecked", Boolean.TRUE.equals(u.getAtivo()));

        // lista de gestores (apenas GESTOR ativos)
        var gestores = usuarioService
                .listByPapelAtivo(com.mmx.medimetrix.domain.enums.Papel.GESTOR, true)
                .stream()
                .map(g -> new Object[]{ g.getIdUsuario(), g.getNome() })
                .toList();
        model.addAttribute("gestores", gestores);

        return "admin/unidades-form";
    }


    @PostMapping
    public String criar(@RequestParam String nome,
                        @RequestParam Long gestorUsuarioId,
                        @RequestParam(required = false) String ativo,
                        org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

        boolean ativoBool = (ativo != null);
        var created = unidadeService.create(new UnidadeCreate(nome != null ? nome.trim() : "", gestorUsuarioId));
        if (!ativoBool) unidadeService.deactivate(created.getIdUnidade());

        ra.addFlashAttribute("flashSuccess", "Unidade criada com sucesso.");
        return "redirect:/app/admin/unidades";
    }





    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @RequestParam(required = false) String nome,
                            @RequestParam(required = false) Long gestorUsuarioId,
                            @RequestParam(required = false) String ativo,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

        Boolean ativoBool = (ativo != null) ? Boolean.TRUE : Boolean.FALSE; // checkbox ausente = false
        var cmd = new com.mmx.medimetrix.application.unidade.commands.UnidadeUpdate(
                id,
                (nome != null && !nome.isBlank()) ? nome.trim() : null,
                gestorUsuarioId,
                ativoBool
        );
        unidadeService.update(cmd);
        ra.addFlashAttribute("flashSuccess", "Unidade atualizada com sucesso.");
        return "redirect:/app/admin/unidades";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        unidadeService.deactivate(id);
        ra.addFlashAttribute("flashSuccess", "Unidade desativada.");
        return "redirect:/app/admin/unidades";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        unidadeService.activate(id);
        ra.addFlashAttribute("flashSuccess", "Unidade ativada.");
        return "redirect:/app/admin/unidades";
    }

}

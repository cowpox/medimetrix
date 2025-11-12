package com.mmx.medimetrix.web.mvc.controller.admin;

import com.mmx.medimetrix.application.usuario.queries.UsuarioFiltro;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.web.api.v1.usuario.UsuarioMapper;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioCreateDTO;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioResponseDTO;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioUpdateDTO;
import com.mmx.medimetrix.domain.enums.Papel;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/app/admin/usuarios")
public class UsuarioPageController {

    private final UsuarioService service;

    public UsuarioPageController(UsuarioService service) {
        this.service = service;
    }

    // LISTA
    // LISTA
    @GetMapping
    public String list(@RequestParam(required = false) String termo,
                       @RequestParam(required = false) Papel papel,
                       @RequestParam(required = false) String ativo,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(defaultValue = "NOME") String sort,
                       @RequestParam(defaultValue = "true") boolean asc,
                       Model model) {

        // 🔹 1) Converter o parâmetro de status (String -> Boolean ou null)
        Boolean ativoBool = null;
        if (ativo != null && !ativo.isBlank()) {
            ativoBool = Boolean.valueOf(ativo);
        }

        // 🔹 2) Construir filtro
        var filtro = new UsuarioFiltro(termo, papel, ativoBool);

        // 🔹 3) Buscar dados
        var resultado = service.search(filtro, page, size, sort, asc);
        List<UsuarioResponseDTO> itens = resultado.stream()
                .map(UsuarioMapper::toResponse)
                .toList();

        // 🔹 4) Paginação
        model.addAttribute("itens", itens);
        model.addAttribute("pageIdx", page);
        model.addAttribute("size", size);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", itens.size() == size);

        // 🔹 5) Filtros e metadados para view
        model.addAttribute("pageTitle", "Usuários");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("termo", termo);
        model.addAttribute("papel", papel);
        model.addAttribute("ativo", ativo);
        model.addAttribute("sort", sort);
        model.addAttribute("asc", asc);

        return "admin/usuarios-list";
    }


    // NOVO
    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UsuarioCreateDTO(null, null, null));
        }
        model.addAttribute("pageTitle", "Novo Usuário");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", false);
        return "admin/usuarios-form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") UsuarioCreateDTO form,
                        BindingResult br,
                        RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
            ra.addFlashAttribute("form", form);
            return "redirect:/app/admin/usuarios/novo";
        }

        service.create(UsuarioMapper.toCreateCommand(form));
        ra.addFlashAttribute("flashSuccess", "Usuário criado com sucesso.");
        return "redirect:/app/admin/usuarios";
    }

    // EDITAR
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var dto = UsuarioMapper.toResponse(service.getById(id));

        if (!model.containsAttribute("form")) {
            var form = new UsuarioUpdateDTO(dto.nome(), dto.email(), dto.papel(), dto.ativo());
            model.addAttribute("form", form);
        }

        model.addAttribute("id", id);
        model.addAttribute("pageTitle", "Editar Usuário");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", true);
        return "admin/usuarios-form";
    }

    @PostMapping(value = "/{id}", params = "_method=put")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") UsuarioUpdateDTO form,
                            BindingResult br,
                            RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
            ra.addFlashAttribute("form", form);
            return "redirect:/app/admin/usuarios/{id}/editar";
        }

        service.update(UsuarioMapper.toUpdateCommand(id, form));
        ra.addFlashAttribute("flashSuccess", "Usuário atualizado com sucesso.");
        return "redirect:/app/admin/usuarios";
    }

    // AÇÕES
    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes ra) {
        service.desativar(id);
        ra.addFlashAttribute("flashSuccess", "Usuário desativado.");
        return "redirect:/app/admin/usuarios";
    }

    @PostMapping("/{id}/ativar")
    public String reativar(@PathVariable Long id, RedirectAttributes ra) {
        service.reativar(id);
        ra.addFlashAttribute("flashSuccess", "Usuário reativado.");
        return "redirect:/app/admin/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("flashSuccess", "Usuário excluído.");
        return "redirect:/app/admin/usuarios";
    }
}

package com.mmx.medimetrix.web.mvc.controller.admin;

import com.mmx.medimetrix.application.medico.commands.MedicoCreate;
import com.mmx.medimetrix.application.medico.commands.MedicoUpdate;
import com.mmx.medimetrix.application.medico.exceptions.CrmDuplicadoException;
import com.mmx.medimetrix.application.medico.exceptions.MedicoNaoEncontradoException;
import com.mmx.medimetrix.application.medico.service.MedicoService;

import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;

import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;

import com.mmx.medimetrix.domain.core.Medico;
import com.mmx.medimetrix.domain.core.Usuario;
import com.mmx.medimetrix.domain.core.Especialidade;
import com.mmx.medimetrix.domain.core.Unidade;
import com.mmx.medimetrix.domain.enums.Papel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/admin/medicos")
public class MedicoPageController {

    private final MedicoService medicoService;
    private final UsuarioService usuarioService;
    private final EspecialidadeService especialidadeService;
    private final UnidadeService unidadeService;

    public MedicoPageController(MedicoService medicoService,
                                UsuarioService usuarioService,
                                EspecialidadeService especialidadeService,
                                UnidadeService unidadeService) {
        this.medicoService = medicoService;
        this.usuarioService = usuarioService;
        this.especialidadeService = especialidadeService;
        this.unidadeService = unidadeService;
    }

    // View Model só para a tela
    public record MedicoVM(
            Long id,                  // = usuarioId (usado na URL)
            String nome,
            String crmNumero,
            String crmUf,
            Long especialidadeId,
            String especialidadeNome,
            Long unidadeId,
            String unidadeNome,
            Boolean ativo
    ) {}

    /** GET /app/admin/medicos */
    @GetMapping
    public String list(@RequestParam(required = false) String termo,          // nome ou CRM
                       @RequestParam(required = false) Long especialidadeId,
                       @RequestParam(required = false) Long unidadeId,
                       @RequestParam(required = false) Boolean ativo,         // null=todos
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(defaultValue = "nome") String sort,      // <-- NOVO
                       @RequestParam(defaultValue = "asc") String dir,        // <-- NOVO
                       Model model) {

        // ---------------------------------------------------------
        // 1) Carrega TODOS os médicos relevantes (sem termo ainda)
        //    Usamos page=0 e um size alto para não perder ninguém.
        //    Se depois o volume crescer, vale mover esse filtro p/ o service.
        // ---------------------------------------------------------
        final int BIG_SIZE = 1000;

        List<Medico> base;
        if (especialidadeId != null) {
            base = medicoService.listByEspecialidade(especialidadeId, 0, BIG_SIZE);
        } else if (unidadeId != null) {
            base = medicoService.listByUnidade(unidadeId, 0, BIG_SIZE);
        } else {
            base = medicoService.listPaged(0, BIG_SIZE);
        }

        // 2) Enriquecer com Usuario / Especialidade / Unidade
        Set<Long> usuarioIds = base.stream().map(Medico::getUsuarioId).collect(Collectors.toSet());
        Set<Long> espIds = base.stream().map(Medico::getEspecialidadeId).collect(Collectors.toSet());
        Set<Long> unIds  = base.stream().map(Medico::getUnidadeId).collect(Collectors.toSet());

        Map<Long, Usuario> usuarios = new HashMap<>();
        Map<Long, Especialidade> especialidades = new HashMap<>();
        Map<Long, Unidade> unidades = new HashMap<>();

        for (Long id : usuarioIds) {
            try { usuarios.put(id, usuarioService.getById(id)); } catch (Exception ignore) {}
        }
        for (Long id : espIds) {
            especialidadeService.findById(id).ifPresent(e -> especialidades.put(id, e));
        }
        for (Long id : unIds) {
            unidadeService.findById(id).ifPresent(u -> unidades.put(id, u));
        }

        List<MedicoVM> vms = base.stream().map(m -> {
            Usuario u = usuarios.get(m.getUsuarioId());
            Especialidade e = especialidades.get(m.getEspecialidadeId());
            Unidade un = unidades.get(m.getUnidadeId());
            return new MedicoVM(
                    m.getUsuarioId(),
                    u != null ? u.getNome() : "—",
                    m.getCrmNumero(),
                    m.getCrmUf(),
                    m.getEspecialidadeId(),
                    e != null ? e.getNome() : "—",
                    m.getUnidadeId(),
                    un != null ? un.getNome() : "—",
                    u != null ? u.getAtivo() : null
            );
        }).toList();

        // ---------------------------------------------------------
        // 3) Filtros de termo e status em memória,
        //    agora em cima da lista COMPLETA.
        // ---------------------------------------------------------
        List<MedicoVM> filtrado = vms.stream()
                .filter(vm -> ativo == null || Objects.equals(vm.ativo(), ativo))
                .filter(vm -> {
                    if (termo == null || termo.isBlank()) return true;
                    String t = termo.toLowerCase();
                    return (vm.nome() != null && vm.nome().toLowerCase().contains(t))
                            || (vm.crmNumero() != null && vm.crmNumero().toLowerCase().contains(t));
                })
                .toList();

        // 4) Ordenação em memória (nome ou crm)
        Comparator<MedicoVM> comparator;
        if ("crm".equalsIgnoreCase(sort)) {
            comparator = Comparator.comparing(
                    MedicoVM::crmNumero,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
        } else {
            // default: nome
            comparator = Comparator.comparing(
                    MedicoVM::nome,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
        }
        if ("desc".equalsIgnoreCase(dir)) {
            comparator = comparator.reversed();
        }

        List<MedicoVM> ordenado = filtrado.stream()
                .sorted(comparator)
                .toList();

        // 5) Paginação em memória
        int pageSize = Math.max(1, size);
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, ordenado.size());

        if (fromIndex >= ordenado.size()) {
            page = 0;
            fromIndex = 0;
            toIndex = Math.min(pageSize, ordenado.size());
        }

        List<MedicoVM> pageItems = ordenado.subList(fromIndex, toIndex);

        boolean hasPrev = page > 0;
        boolean hasNext = toIndex < ordenado.size();

        // 6) Combos
        var especialidadesCombo = especialidadeService.listAtivas();
        var unidadesCombo = unidadeService.list(
                new UnidadeFiltro(null, 0, 200, "nome", true, true)
        );

        // 7) Model
        model.addAttribute("pageTitle", "Médicos");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("itens", pageItems);

        model.addAttribute("termo", termo);
        model.addAttribute("especialidadeId", especialidadeId);
        model.addAttribute("unidadeId", unidadeId);
        model.addAttribute("ativo", ativo == null ? "" : ativo.toString());
        model.addAttribute("especialidades", especialidadesCombo);
        model.addAttribute("unidades", unidadesCombo);

        model.addAttribute("pageIdx", page);
        model.addAttribute("size", pageSize);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);

        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        return "admin/medicos-list";
    }


    /** GET /novo */
    @GetMapping("/novo")
    public String novo(Model model) {

        // 1) Usuários com papel MEDICO e ativos
        var medicosAtivos = usuarioService.listByPapelAtivo(Papel.MEDICO, true);

        // 2) Buscar médicos já cadastrados (ajuste o tamanho se precisar)
        var medicosCadastrados = medicoService.listPaged(0, 10_000);
        var usuariosJaVinculados = medicosCadastrados.stream()
                .map(Medico::getUsuarioId)
                .collect(Collectors.toSet());

        // 3) Elegíveis = médicos ativos que AINDA NÃO têm registro em MEDICO
        var usuariosElegiveis = medicosAtivos.stream()
                .filter(u -> !usuariosJaVinculados.contains(u.getIdUsuario()))
                .toList();

        model.addAttribute("pageTitle", "Novo Médico");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", false);

        model.addAttribute("especialidades", especialidadeService.listAtivas());
        model.addAttribute("unidades", unidadeService.list(
                new UnidadeFiltro(null, 0, 200, "nome", true, true)
        ));

        // aqui já vai só a lista filtrada
        model.addAttribute("usuariosElegiveis", usuariosElegiveis);

        return "admin/medicos-form";
    }


    /** GET /{id}/editar  (id == usuarioId do médico) */
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Medico m = medicoService.findByUsuarioId(id)
                .orElseThrow(MedicoNaoEncontradoException::new);
        Usuario u = null;
        try {
            u = usuarioService.getById(id);
        } catch (Exception ignore) {}

        model.addAttribute("pageTitle", "Editar Médico");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("isEdit", true);
        model.addAttribute("id", id);

        model.addAttribute("especialidades", especialidadeService.listAtivas());
        model.addAttribute("unidades", unidadeService.list(
                new UnidadeFiltro(null, 0, 200, "nome", true, true)
        ));
        model.addAttribute("medico", m);
        model.addAttribute("usuarioNome", u != null ? u.getNome() : "—");

        return "admin/medicos-form";
    }

    /** POST criar */
    @PostMapping
    public String criar(@RequestParam Long usuarioId,
                        @RequestParam Long especialidadeId,
                        @RequestParam Long unidadeId,
                        @RequestParam String crmNumero,
                        @RequestParam String crmUf,
                        RedirectAttributes ra) {
        try {
            MedicoCreate cmd = new MedicoCreate();
            cmd.setUsuarioId(usuarioId);
            cmd.setEspecialidadeId(especialidadeId);
            cmd.setUnidadeId(unidadeId);
            cmd.setCrmNumero(crmNumero);
            cmd.setCrmUf(crmUf);

            medicoService.create(cmd);
            ra.addFlashAttribute("success", "Médico criado com sucesso.");
            return "redirect:/app/admin/medicos";

        } catch (CrmDuplicadoException e) {
            ra.addFlashAttribute("error",
                    "Já existe médico com CRM " + crmNumero + "-" + crmUf + ".");
            return "redirect:/app/admin/medicos/novo";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Não foi possível criar o médico.");
            return "redirect:/app/admin/medicos/novo";
        }
    }

    /** POST update (PUT sem _method real, só na navegação) */
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @RequestParam(required = false) Long especialidadeId,
                            @RequestParam(required = false) Long unidadeId,
                            @RequestParam(required = false) String crmNumero,
                            @RequestParam(required = false) String crmUf,
                            RedirectAttributes ra) {
        try {
            MedicoUpdate cmd = new MedicoUpdate();
            cmd.setEspecialidadeId(especialidadeId);
            cmd.setUnidadeId(unidadeId);
            cmd.setCrmNumero(crmNumero);
            cmd.setCrmUf(crmUf);

            medicoService.update(id, cmd);
            ra.addFlashAttribute("success", "Médico atualizado com sucesso.");
            return "redirect:/app/admin/medicos";

        } catch (CrmDuplicadoException e) {
            ra.addFlashAttribute("error",
                    "Já existe médico com CRM " + crmNumero + "-" + crmUf + ".");
            return "redirect:/app/admin/medicos/" + id + "/editar";
        } catch (MedicoNaoEncontradoException e) {
            ra.addFlashAttribute("error", "Médico não encontrado.");
            return "redirect:/app/admin/medicos";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Não foi possível atualizar o médico.");
            return "redirect:/app/admin/medicos/" + id + "/editar";
        }
    }

    /** POST delete */
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            boolean deleted = medicoService.deleteIfSemVinculos(id);

            if (deleted) {
                ra.addFlashAttribute("success", "Médico excluído com sucesso.");
            } else {
                ra.addFlashAttribute("error",
                        "Este médico já possui avaliações/participações vinculadas e não pode ser excluído. " +
                                "Desative o usuário na tela de Usuários para impedir novos acessos.");
            }

        } catch (MedicoNaoEncontradoException e) {
            ra.addFlashAttribute("error", "Médico não encontrado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Não foi possível excluir o médico.");
        }
        return "redirect:/app/admin/medicos";
    }

}

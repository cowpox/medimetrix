package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.application.avaliacao.service.AvaliacaoService;
import com.mmx.medimetrix.application.relatorio.service.RelatorioAvaliacaoService;
import com.mmx.medimetrix.application.relatorio.vm.AvaliacaoResumoVM;
import com.mmx.medimetrix.application.relatorio.vm.RelatorioAvaliacaoDetalheVM;
import com.mmx.medimetrix.application.relatorio.vm.RelatorioAvaliacaoMedicoVM;
import com.mmx.medimetrix.domain.core.Avaliacao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/app/gestor/relatorios/avaliacoes")
public class RelatoriosAvaliacaoPageController {

    private final AvaliacaoService avaliacaoService;
    private final RelatorioAvaliacaoService relatorioAvaliacaoService;

    public RelatoriosAvaliacaoPageController(AvaliacaoService avaliacaoService,
                                             RelatorioAvaliacaoService relatorioAvaliacaoService) {
        this.avaliacaoService = avaliacaoService;
        this.relatorioAvaliacaoService = relatorioAvaliacaoService;
    }

    // ================================
    // Lista de Avaliações para Relatório
    // ================================
    @GetMapping
    public String list(@RequestParam(name = "termo", required = false) String termo,
                       @RequestParam(name = "status", required = false) String status,
                       Model model) {

        List<AvaliacaoResumoVM> avaliacoes = relatorioAvaliacaoService.listarAvaliacoes(termo, status);

        model.addAttribute("pageTitle", "Relatórios de Avaliações");
        model.addAttribute("breadcrumb", "Relatórios");
        model.addAttribute("termo", termo);
        model.addAttribute("status", status);
        model.addAttribute("avaliacoes", avaliacoes);

        return "gestor/relatorios-avaliacoes-list";
    }

    // ================================
    // Relatório Consolidado da Avaliação
    // ================================
    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {

        Avaliacao avaliacao = avaliacaoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        RelatorioAvaliacaoDetalheVM relatorio = relatorioAvaliacaoService.detalharAvaliacao(id);

        model.addAttribute("pageTitle", "Relatório da Avaliação");
        model.addAttribute("breadcrumb", "Relatórios");
        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("resumo", relatorio.resumo());
        model.addAttribute("participacoes", relatorio.participacoes());

        return "gestor/relatorios-avaliacao-detalhe";
    }

    // ==========================================
    // Relatório por médico dentro da avaliação
    // ==========================================
    @GetMapping("/{id}/medico/{idMedico}")
    public String detalheMedico(@PathVariable Long id,
                                @PathVariable Long idMedico,
                                Model model) {

        Avaliacao avaliacao = avaliacaoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        RelatorioAvaliacaoMedicoVM relatorio =
                relatorioAvaliacaoService.detalharAvaliacaoPorMedico(id, idMedico);

        model.addAttribute("pageTitle", "Relatório por médico");
        model.addAttribute("breadcrumb", "Relatórios");
        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("resumo", relatorio.resumo());
        model.addAttribute("questoes", relatorio.questoes());
        model.addAttribute("criterios", relatorio.criterios());

        return "gestor/relatorios-avaliacao-medico";
    }
}

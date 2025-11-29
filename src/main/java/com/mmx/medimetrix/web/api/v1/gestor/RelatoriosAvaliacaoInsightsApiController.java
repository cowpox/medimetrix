package com.mmx.medimetrix.web.api.v1.gestor;

import com.mmx.medimetrix.application.relatorio.service.RelatorioAvaliacaoInsightsService;
import com.mmx.medimetrix.application.relatorio.vm.RelatorioMedicoInsightsVM;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API de Insights (Analytics Avançado / Insights com IA)
 * para o relatório por médico.
 */
@RestController
@RequestMapping("/app/api/gestor/relatorios/avaliacoes")
public class RelatoriosAvaliacaoInsightsApiController {

    private final RelatorioAvaliacaoInsightsService insightsService;

    public RelatoriosAvaliacaoInsightsApiController(RelatorioAvaliacaoInsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @GetMapping("/{idAvaliacao}/medico/{idMedico}/insights")
    public RelatorioMedicoInsightsVM gerarInsights(@PathVariable Long idAvaliacao,
                                                   @PathVariable Long idMedico) {
        return insightsService.gerarInsightsParaMedico(idAvaliacao, idMedico);
    }
}

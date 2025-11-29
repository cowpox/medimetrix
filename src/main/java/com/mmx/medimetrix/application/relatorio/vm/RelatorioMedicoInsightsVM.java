package com.mmx.medimetrix.application.relatorio.vm;

import java.util.List;

/**
 * ViewModel retornado pela API de Insights.
 * Leva para o front:
 *  - o resumo do médico na avaliação,
 *  - os dados por critério (mesmos do radar),
 *  - textos interpretativos (visão geral, pontos fortes, atenção, sugestões).
 *
 * Pode ser preenchido por heurística ou por LLM, sem mudar a estrutura.
 */
public record RelatorioMedicoInsightsVM(
        AvaliacaoMedicoResumoVM resumo,
        List<CriterioRadarVM> criterios,
        String visaoGeral,
        String destaquesPositivos,
        String pontosAtencao,
        String sugestoesDesenvolvimento
) {}

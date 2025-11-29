package com.mmx.medimetrix.application.relatorio.vm;

/**
 * Um bloco de insight (ex.: "Pontos fortes", "Oportunidades de melhoria", etc.).
 * O texto pode vir em markdown ou texto simples.
 */
public record RelatorioMedicoInsightVM(
        String categoria, // ex.: RESUMO, PONTOS_FORTES, PONTOS_ATENCAO
        String titulo,
        String texto
) {}

package com.mmx.medimetrix.application.relatorio.service;

import com.mmx.medimetrix.application.relatorio.vm.RelatorioMedicoInsightsVM;

/**
 * Serviço responsável por gerar insights avançados sobre o desempenho
 * do médico em uma avaliação (heurística + LLM quando disponível).
 */
public interface RelatorioAvaliacaoInsightsService {

    /**
     * Gera os textos interpretativos (visão geral, destaques, pontos de atenção
     * e sugestões) para um médico em uma determinada avaliação.
     *
     * @param idAvaliacao id da avaliação
     * @param idMedico    id do médico (id do USUARIO)
     * @return ViewModel com resumo, critérios e textos de insights
     */
    RelatorioMedicoInsightsVM gerarInsightsParaMedico(Long idAvaliacao, Long idMedico);
}

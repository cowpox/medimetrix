package com.mmx.medimetrix.application.relatorio.service;

import com.mmx.medimetrix.application.relatorio.vm.AvaliacaoResumoVM;
import com.mmx.medimetrix.application.relatorio.vm.RelatorioAvaliacaoDetalheVM;
import com.mmx.medimetrix.application.relatorio.vm.RelatorioAvaliacaoMedicoVM;

import java.util.List;

public interface RelatorioAvaliacaoService {

    /**
     * Lista as avaliações que podem ser usadas em relatórios,
     * já filtrando por título e status e calculando os KPIs básicos
     * de participação e adesão.
     */
    List<AvaliacaoResumoVM> listarAvaliacoes(String termo, String status);

    /**
     * Retorna o resumo da avaliação e a lista de participações por médico,
     * com status e nota média de cada um.
     */
    RelatorioAvaliacaoDetalheVM detalharAvaliacao(Long idAvaliacao);

    /**
     * Retorna o cabeçalho e as notas por questão para um médico específico
     * dentro de uma avaliação.
     */
    RelatorioAvaliacaoMedicoVM detalharAvaliacaoPorMedico(Long idAvaliacao, Long idMedico);
}

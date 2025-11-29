package com.mmx.medimetrix.application.relatorio.vm;

import java.util.List;

public record RelatorioAvaliacaoDetalheVM(
        AvaliacaoResumoVM resumo,
        List<ParticipacaoResumoVM> participacoes
) {}

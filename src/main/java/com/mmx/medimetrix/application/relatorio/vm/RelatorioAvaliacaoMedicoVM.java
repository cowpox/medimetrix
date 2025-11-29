package com.mmx.medimetrix.application.relatorio.vm;

import java.util.List;

public record RelatorioAvaliacaoMedicoVM(
        AvaliacaoMedicoResumoVM resumo,
        List<QuestaoNotaVM> questoes
) {}

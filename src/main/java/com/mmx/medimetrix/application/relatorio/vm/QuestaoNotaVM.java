package com.mmx.medimetrix.application.relatorio.vm;

import java.math.BigDecimal;

public record QuestaoNotaVM(
        int ordem,
        BigDecimal nota,           // nota original do médico (escala original)
        BigDecimal notaMaxima,     // máximo da escala (5, 10, etc.)
        String textoQuestao,
        BigDecimal notaGrupoMin,   // MIN do grupo (normalizado para 0–5)
        BigDecimal notaGrupoMax,    // MAX do grupo (normalizado para 0–5)
        BigDecimal notaGrupoMedia // Media do grupo (normalizado para 0-5)
) {}

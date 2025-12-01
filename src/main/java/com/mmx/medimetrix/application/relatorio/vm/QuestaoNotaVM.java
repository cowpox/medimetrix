package com.mmx.medimetrix.application.relatorio.vm;

import java.math.BigDecimal;

public record QuestaoNotaVM(
        int ordem,
        BigDecimal nota,           // nota original do médico (escala original)
        BigDecimal notaMaxima,     // máximo da escala (5, 10, etc.)
        String textoQuestao,
        BigDecimal notaGrupoMin,   // MIN do grupo (normalizado para 0–5)
        BigDecimal notaGrupoMax,   // MAX do grupo (normalizado para 0–5)
        BigDecimal notaGrupoMedia, // Média do grupo (normalizado para 0–5)
        boolean sensivel,
        boolean visivelParaGestor
) {

    /**
     * Para a visão do GESTOR, a nota individual deve ser mascarada
     * se a questão for sensível OU se estiver marcada como “oculta p/ gestor”.
     */
    public boolean mascararNotaGestor() {
        return sensivel || !visivelParaGestor;
    }
}


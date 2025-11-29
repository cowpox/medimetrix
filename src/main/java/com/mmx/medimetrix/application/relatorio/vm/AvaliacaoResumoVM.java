package com.mmx.medimetrix.application.relatorio.vm;

import java.math.BigDecimal;

/**
 * Resumo de uma avaliação para telas de listagem e cabeçalhos.
 */
public record AvaliacaoResumoVM(
        Long id,
        String titulo,
        String periodo,
        String status,
        long totalParticipacoes,
        long respondidas,
        long emAndamento,
        long pendentes,
        BigDecimal adesaoPercent
) {

    public String statusLabel() {
        return switch (status == null ? "" : status.toUpperCase()) {
            case "PUBLICADA" -> "Publicada";
            case "ENCERRADA" -> "Encerrada";
            case "RASCUNHO"  -> "Rascunho";
            default -> status;
        };
    }

    public String statusBadgeClass() {
        return switch (status == null ? "" : status.toUpperCase()) {
            case "PUBLICADA" -> "bg-primary text-white";
            case "ENCERRADA" -> "bg-secondary text-white";
            case "RASCUNHO"  -> "bg-warning text-dark";
            default -> "bg-light text-dark";
        };
    }
}

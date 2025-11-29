package com.mmx.medimetrix.application.relatorio.vm;

import java.math.BigDecimal;

public record AvaliacaoMedicoResumoVM(
        Long idAvaliacao,
        String tituloAvaliacao,
        String periodo,
        String statusLabel,
        String statusBadgeClass,
        Long idMedico,
        String nomeMedico,
        String nomeEspecialidade,
        String nomeUnidade,
        String statusParticipacao,

        // Estatísticas do médico
        BigDecimal media,
        BigDecimal menorNota,
        BigDecimal maiorNota,
        Integer totalRespostas,

        // NOVOS CAMPOS – estatísticas do grupo
        BigDecimal mediaGrupo,
        BigDecimal menorNotaGrupo,
        BigDecimal maiorNotaGrupo
) {}

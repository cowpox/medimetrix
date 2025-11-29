package com.mmx.medimetrix.application.relatorio.vm;

import java.math.BigDecimal;

public record ParticipacaoResumoVM(
        Long idParticipacao,
        Long idMedico,
        String nomeMedico,
        String status,
        BigDecimal notaMedia
) {}

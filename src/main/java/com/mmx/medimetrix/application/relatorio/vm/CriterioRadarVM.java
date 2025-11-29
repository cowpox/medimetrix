package com.mmx.medimetrix.application.relatorio.vm;

import java.math.BigDecimal;

public record CriterioRadarVM(
        Long idCriterio,
        String nomeCriterio,
        BigDecimal notaMedico,   // média do médico nesse critério (0–5)
        BigDecimal notaGrupo     // média do grupo nesse critério (0–5)
) {}

package com.mmx.medimetrix.application.meta.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Campos todos opcionais para update parcial (PATCH-like via PUT). */
public record MetaUpdate(
        Long idCriterio,
        Long idUnidade,
        Long idEspecialidade,
        BigDecimal alvo,
        String operador,          // deve respeitar CK ('>=','<=','=')
        LocalDate vigenciaInicio, // respeita CK de período
        LocalDate vigenciaFim,
        Boolean ativo,
        Integer prioridade,
        String justificativa
) {}

package com.mmx.medimetrix.web.api.v1.meta.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MetaCreateDTO(
        @NotNull Long idCriterio,
        Long idUnidade,
        Long idEspecialidade,
        @NotNull BigDecimal alvo,
        @NotNull String operador,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim,
        Integer prioridade,
        String justificativa
) {}

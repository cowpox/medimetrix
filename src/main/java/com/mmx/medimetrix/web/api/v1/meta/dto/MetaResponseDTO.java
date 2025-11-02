package com.mmx.medimetrix.web.api.v1.meta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MetaResponseDTO(
        Long id,
        Long idCriterio,
        Long idUnidade,
        Long idEspecialidade,
        BigDecimal alvo,
        String operador,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim,
        Boolean ativo,
        Integer prioridade,
        String justificativa,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaEdicao
) {}

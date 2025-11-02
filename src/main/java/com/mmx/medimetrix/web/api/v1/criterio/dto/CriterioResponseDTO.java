package com.mmx.medimetrix.web.api.v1.criterio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CriterioResponseDTO(
        Long id,
        String nome,
        String definicaoOperacional,
        String descricao,
        Boolean ativo,
        BigDecimal peso,
        Integer ordemSugerida,
        Integer versao,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaEdicao
) {}

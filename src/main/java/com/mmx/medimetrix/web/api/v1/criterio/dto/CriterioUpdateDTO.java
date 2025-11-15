package com.mmx.medimetrix.web.api.v1.criterio.dto;

import java.math.BigDecimal;

public record CriterioUpdateDTO(
        String nome,
        String definicaoOperacional,
        String descricao,
        BigDecimal peso,
        Integer ordemSugerida,
        Boolean ativo
) {}

package com.mmx.medimetrix.web.api.v1.criterio.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CriterioCreateDTO(
        @NotBlank String nome,
        String definicaoOperacional,
        String descricao,
        BigDecimal peso,
        Integer ordemSugerida,
        Boolean ativo
) {}

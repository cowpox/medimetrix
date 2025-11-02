package com.mmx.medimetrix.web.api.v1.criterio.dto;

import jakarta.validation.constraints.NotBlank;

public record CriterioCreateDTO(
        @NotBlank String nome,
        String definicaoOperacional,
        String descricao
) {}

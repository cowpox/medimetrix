package com.mmx.medimetrix.web.api.v1.unidade.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO para atualização parcial de Unidade.
 */
public record UnidadeUpdateDTO(
        @Size(min = 2, max = 120) String nome,
        Long gestorUsuarioId,
        Boolean ativo
) {}

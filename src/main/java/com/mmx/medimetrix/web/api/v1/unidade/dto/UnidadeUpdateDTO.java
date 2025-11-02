package com.mmx.medimetrix.web.api.v1.unidade.dto;

import jakarta.validation.constraints.Size;

public record UnidadeUpdateDTO(
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres.")
        String nome,
        Boolean ativo
) {}

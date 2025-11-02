package com.mmx.medimetrix.web.api.v1.criterio.dto;

public record CriterioUpdateDTO(
        String nome,
        String definicaoOperacional,
        String descricao,
        Boolean ativo
) {}

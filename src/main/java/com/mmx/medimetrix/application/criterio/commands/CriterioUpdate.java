package com.mmx.medimetrix.application.criterio.commands;

public record CriterioUpdate(
        String nome,
        String definicaoOperacional,
        String descricao,
        Boolean ativo
) {}

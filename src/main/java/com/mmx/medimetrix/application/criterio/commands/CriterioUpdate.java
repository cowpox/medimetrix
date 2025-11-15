package com.mmx.medimetrix.application.criterio.commands;

import java.math.BigDecimal;

public record CriterioUpdate(
        String nome,
        String definicaoOperacional,
        String descricao,
        BigDecimal peso,
        Integer ordemSugerida,
        Boolean ativo
) {}

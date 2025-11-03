package com.mmx.medimetrix.application.unidade.commands;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Comando para atualização de uma Unidade existente.
 */
public record UnidadeUpdate(
        @NotNull Long id,
        @Size(min = 2, max = 120) String nome,
        Long gestorUsuarioId,
        Boolean ativo
) {}

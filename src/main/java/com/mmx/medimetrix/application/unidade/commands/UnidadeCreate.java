package com.mmx.medimetrix.application.unidade.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UnidadeCreate(
        @NotBlank @Size(max = 120) String nome
) {}

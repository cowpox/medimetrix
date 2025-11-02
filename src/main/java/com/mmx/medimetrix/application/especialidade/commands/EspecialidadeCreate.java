package com.mmx.medimetrix.application.especialidade.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadeCreate(
        @NotBlank @Size(max = 120) String nome
) {}

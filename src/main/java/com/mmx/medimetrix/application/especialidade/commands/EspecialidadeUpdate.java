package com.mmx.medimetrix.application.especialidade.commands;

import jakarta.validation.constraints.Size;

public record EspecialidadeUpdate(
        @Size(max = 120) String nome,
        Boolean ativo
) {}

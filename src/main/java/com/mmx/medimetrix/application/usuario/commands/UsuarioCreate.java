package com.mmx.medimetrix.application.usuario.commands;

import com.mmx.medimetrix.domain.enums.Papel;

public record UsuarioCreate(
        String nome,
        String email,
        Papel papel
) {}

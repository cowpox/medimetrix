package com.mmx.medimetrix.application.usuario.commands;

import com.mmx.medimetrix.domain.enums.Papel;

public record UsuarioUpdate(
        Long idUsuario,
        String nome,
        String email,
        Papel papel,
        Boolean ativo
) { }

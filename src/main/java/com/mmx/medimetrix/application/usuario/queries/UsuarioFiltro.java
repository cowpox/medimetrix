package com.mmx.medimetrix.application.usuario.queries;

import com.mmx.medimetrix.domain.enums.Papel;

public record UsuarioFiltro(
        String termo,
        Papel papel,
        Boolean ativo
) {}

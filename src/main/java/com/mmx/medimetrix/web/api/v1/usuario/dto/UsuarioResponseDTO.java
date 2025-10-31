package com.mmx.medimetrix.web.api.v1.usuario.dto;

import com.mmx.medimetrix.domain.enums.Papel;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Papel papel,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaEdicao
) {}

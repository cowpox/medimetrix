package com.mmx.medimetrix.web.api.v1.especialidade.dto;

import java.time.LocalDateTime;

public record EspecialidadeResponseDTO(
        Long id,
        String nome,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaEdicao
) {}

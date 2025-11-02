package com.mmx.medimetrix.web.api.v1.unidade.dto;

import java.time.LocalDateTime;

public record UnidadeResponseDTO(
        Long id,
        String nome,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaEdicao
) {}

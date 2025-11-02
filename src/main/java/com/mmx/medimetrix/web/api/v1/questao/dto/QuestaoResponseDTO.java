package com.mmx.medimetrix.web.api.v1.questao.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuestaoResponseDTO(
        Long id,
        Long idCriterio,
        String enunciado,
        String tipo,
        String obrigatoriedade,
        BigDecimal validacaoNumMin,
        BigDecimal validacaoNumMax,
        Integer tamanhoTextoMax,
        Boolean sensivel,
        Boolean visivelParaGestor,
        Integer versao,
        Integer ordemSugerida,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaEdicao
) {}

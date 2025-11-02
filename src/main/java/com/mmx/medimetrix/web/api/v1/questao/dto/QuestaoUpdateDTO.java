package com.mmx.medimetrix.web.api.v1.questao.dto;

import java.math.BigDecimal;

public record QuestaoUpdateDTO(
        String enunciado,
        String tipo,
        String obrigatoriedade,
        BigDecimal validacaoNumMin,
        BigDecimal validacaoNumMax,
        Integer tamanhoTextoMax,
        Boolean sensivel,
        Boolean visivelParaGestor,
        Boolean ativo,
        Integer ordemSugerida
) {}

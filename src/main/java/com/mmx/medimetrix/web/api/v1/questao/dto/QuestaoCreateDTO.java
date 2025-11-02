package com.mmx.medimetrix.web.api.v1.questao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record QuestaoCreateDTO(
        @NotNull Long idCriterio,
        @NotBlank String enunciado,
        String tipo,
        String obrigatoriedade,
        BigDecimal validacaoNumMin,
        BigDecimal validacaoNumMax,
        Integer tamanhoTextoMax,
        Boolean sensivel,
        Boolean visivelParaGestor,
        Integer ordemSugerida
) {}

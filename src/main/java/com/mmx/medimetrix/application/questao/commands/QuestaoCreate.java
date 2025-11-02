package com.mmx.medimetrix.application.questao.commands;

import java.math.BigDecimal;

public record QuestaoCreate(Long idCriterio, String enunciado, String tipo, String obrigatoriedade,
                            BigDecimal validacaoNumMin, BigDecimal validacaoNumMax,
                            Integer tamanhoTextoMax, Boolean sensivel, Boolean visivelParaGestor, Integer ordemSugerida) {}

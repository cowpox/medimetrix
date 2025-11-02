package com.mmx.medimetrix.application.avaliacaoquestao.exceptions;

public class AvaliacaoQuestaoNaoEncontradaException extends RuntimeException {
    public AvaliacaoQuestaoNaoEncontradaException() {
        super("Vínculo Avaliação↔Questão não encontrado");
    }
    public AvaliacaoQuestaoNaoEncontradaException(String message) {
        super(message);
    }
}

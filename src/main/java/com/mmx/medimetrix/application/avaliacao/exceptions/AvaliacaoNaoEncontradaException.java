package com.mmx.medimetrix.application.avaliacao.exceptions;

public class AvaliacaoNaoEncontradaException extends RuntimeException {

    public AvaliacaoNaoEncontradaException() {
        super("Avaliação não encontrada");
    }

    public AvaliacaoNaoEncontradaException(String message) {
        super(message);
    }
}

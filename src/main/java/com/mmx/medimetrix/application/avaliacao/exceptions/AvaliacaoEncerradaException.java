package com.mmx.medimetrix.application.avaliacao.exceptions;

public class AvaliacaoEncerradaException extends RuntimeException {

    public AvaliacaoEncerradaException() {
        super("A avaliação já foi encerrada e não permite mais alterações.");
    }

    public AvaliacaoEncerradaException(String message) {
        super(message);
    }
}

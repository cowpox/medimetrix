package com.mmx.medimetrix.application.avaliacao.exceptions;

public class AvaliacaoJaPublicadaException extends RuntimeException {

    public AvaliacaoJaPublicadaException() {
        super("A avaliação já foi publicada e não pode ser alterada.");
    }

    public AvaliacaoJaPublicadaException(String message) {
        super(message);
    }
}

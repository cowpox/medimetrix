package com.mmx.medimetrix.application.usuario.exceptions;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}

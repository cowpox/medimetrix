package com.mmx.medimetrix.application.usuario.exceptions;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String entidade, Object id) {
        super(entidade + " não encontrada(o) com id=" + id);
    }
}

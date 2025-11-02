package com.mmx.medimetrix.application.resposta.exceptions;

public class RespostaNaoEncontradaException extends RuntimeException {
    public RespostaNaoEncontradaException() { super("Resposta não encontrada"); }
    public RespostaNaoEncontradaException(String msg) { super(msg); }
}

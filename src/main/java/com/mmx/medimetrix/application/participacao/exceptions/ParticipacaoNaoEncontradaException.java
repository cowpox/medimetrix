package com.mmx.medimetrix.application.participacao.exceptions;

public class ParticipacaoNaoEncontradaException extends RuntimeException {
    public ParticipacaoNaoEncontradaException() { super("Participação não encontrada"); }
    public ParticipacaoNaoEncontradaException(String msg) { super(msg); }
}

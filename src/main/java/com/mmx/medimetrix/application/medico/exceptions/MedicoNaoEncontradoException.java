package com.mmx.medimetrix.application.medico.exceptions;

public class MedicoNaoEncontradoException extends RuntimeException {
    public MedicoNaoEncontradoException() { super("Médico não encontrado"); }
    public MedicoNaoEncontradoException(String msg) { super(msg); }
}

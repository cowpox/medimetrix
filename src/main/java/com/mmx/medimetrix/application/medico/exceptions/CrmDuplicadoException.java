package com.mmx.medimetrix.application.medico.exceptions;

public class CrmDuplicadoException extends RuntimeException {
    public CrmDuplicadoException(String numero, String uf) {
        super("Já existe médico com CRM " + numero + "-" + uf);
    }
}

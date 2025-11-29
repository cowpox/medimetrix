package com.mmx.medimetrix.application.relatorios.dto;

public class InsightsTextoDTO {

    /** Texto em markdown/HTML retornado pela IA */
    private String conteudo;

    public InsightsTextoDTO() {}
    public InsightsTextoDTO(String conteudo) { this.conteudo = conteudo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
}

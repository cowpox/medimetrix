package com.mmx.medimetrix.web.api.v1.resposta.dto;

import java.math.BigDecimal;

public class RespostaUpdateValoresDTO {
    private BigDecimal valorNumerico;
    private Boolean valorBinario;
    private String texto;

    public BigDecimal getValorNumerico() { return valorNumerico; }
    public void setValorNumerico(BigDecimal valorNumerico) { this.valorNumerico = valorNumerico; }

    public Boolean getValorBinario() { return valorBinario; }
    public void setValorBinario(Boolean valorBinario) { this.valorBinario = valorBinario; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}

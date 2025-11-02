package com.mmx.medimetrix.application.resposta.commands;

import java.math.BigDecimal;

public class RespostaCreate {
    private Long idParticipacao;
    private Long idAvaliacao;
    private Long idQuestao;

    private BigDecimal valorNumerico;
    private Boolean valorBinario;
    private String texto;

    public Long getIdParticipacao() { return idParticipacao; }
    public void setIdParticipacao(Long idParticipacao) { this.idParticipacao = idParticipacao; }

    public Long getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Long idAvaliacao) { this.idAvaliacao = idAvaliacao; }

    public Long getIdQuestao() { return idQuestao; }
    public void setIdQuestao(Long idQuestao) { this.idQuestao = idQuestao; }

    public BigDecimal getValorNumerico() { return valorNumerico; }
    public void setValorNumerico(BigDecimal valorNumerico) { this.valorNumerico = valorNumerico; }

    public Boolean getValorBinario() { return valorBinario; }
    public void setValorBinario(Boolean valorBinario) { this.valorBinario = valorBinario; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}

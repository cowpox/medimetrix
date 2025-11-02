package com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AvaliacaoQuestaoCreateDTO {

    @NotNull
    private Long idQuestao;

    @NotNull @Positive
    private Integer ordem;

    private Boolean obrigatoriaNaAval;     // null -> herda da QUESTAO
    private Boolean ativaNaAval;           // default true no banco
    private BigDecimal pesoNaAval;         // null ou >0
    private Boolean visivelParaGestorLocal;
    private String observacaoAdmin;

    public Long getIdQuestao() { return idQuestao; }
    public void setIdQuestao(Long idQuestao) { this.idQuestao = idQuestao; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public Boolean getObrigatoriaNaAval() { return obrigatoriaNaAval; }
    public void setObrigatoriaNaAval(Boolean obrigatoriaNaAval) { this.obrigatoriaNaAval = obrigatoriaNaAval; }

    public Boolean getAtivaNaAval() { return ativaNaAval; }
    public void setAtivaNaAval(Boolean ativaNaAval) { this.ativaNaAval = ativaNaAval; }

    public BigDecimal getPesoNaAval() { return pesoNaAval; }
    public void setPesoNaAval(BigDecimal pesoNaAval) { this.pesoNaAval = pesoNaAval; }

    public Boolean getVisivelParaGestorLocal() { return visivelParaGestorLocal; }
    public void setVisivelParaGestorLocal(Boolean visivelParaGestorLocal) { this.visivelParaGestorLocal = visivelParaGestorLocal; }

    public String getObservacaoAdmin() { return observacaoAdmin; }
    public void setObservacaoAdmin(String observacaoAdmin) { this.observacaoAdmin = observacaoAdmin; }
}

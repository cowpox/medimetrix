package com.mmx.medimetrix.web.mvc.controller.gestor;

import java.math.BigDecimal;

public class QuestaoForm {

    private Long id;

    private Long idCriterio;
    private String enunciado;

    private String tipo;              // LIKERT_5, BARS_5, OPEN, CHECK, NUM_0_10
    private String obrigatoriedade;   // OPCIONAL | OBRIGATORIA

    private BigDecimal validacaoNumMin;
    private BigDecimal validacaoNumMax;
    private Integer tamanhoTextoMax;

    private Boolean sensivel;
    private Boolean visivelParaGestor;
    private Boolean ativo;

    private Integer ordemSugerida;

    // ========= GETTERS / SETTERS =========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdCriterio() { return idCriterio; }
    public void setIdCriterio(Long idCriterio) { this.idCriterio = idCriterio; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getObrigatoriedade() { return obrigatoriedade; }
    public void setObrigatoriedade(String obrigatoriedade) { this.obrigatoriedade = obrigatoriedade; }

    public BigDecimal getValidacaoNumMin() { return validacaoNumMin; }
    public void setValidacaoNumMin(BigDecimal validacaoNumMin) { this.validacaoNumMin = validacaoNumMin; }

    public BigDecimal getValidacaoNumMax() { return validacaoNumMax; }
    public void setValidacaoNumMax(BigDecimal validacaoNumMax) { this.validacaoNumMax = validacaoNumMax; }

    public Integer getTamanhoTextoMax() { return tamanhoTextoMax; }
    public void setTamanhoTextoMax(Integer tamanhoTextoMax) { this.tamanhoTextoMax = tamanhoTextoMax; }

    public Boolean getSensivel() { return sensivel; }
    public void setSensivel(Boolean sensivel) { this.sensivel = sensivel; }

    public Boolean getVisivelParaGestor() { return visivelParaGestor; }
    public void setVisivelParaGestor(Boolean visivelParaGestor) { this.visivelParaGestor = visivelParaGestor; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Integer getOrdemSugerida() { return ordemSugerida; }
    public void setOrdemSugerida(Integer ordemSugerida) { this.ordemSugerida = ordemSugerida; }

}

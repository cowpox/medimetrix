package com.mmx.medimetrix.web.mvc.controller.gestor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MetaForm {

    private Long id;

    private Long idCriterio;
    private Long idUnidade;
    private Long idEspecialidade;

    private BigDecimal alvo;
    private String operador;           // ">=", "<=", "="

    private LocalDate vigenciaInicio;
    private LocalDate vigenciaFim;

    private Integer prioridade;        // 1 = Alta, 2 = Média, 3 = Baixa
    private Boolean ativo;

    private String justificativa;

    // NOVO: escopo derivado (GLOBAL / UNIDADE / ESPECIALIDADE)
    private String escopo;

    // getters / setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdCriterio() { return idCriterio; }
    public void setIdCriterio(Long idCriterio) { this.idCriterio = idCriterio; }

    public Long getIdUnidade() { return idUnidade; }
    public void setIdUnidade(Long idUnidade) { this.idUnidade = idUnidade; }

    public Long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public BigDecimal getAlvo() { return alvo; }
    public void setAlvo(BigDecimal alvo) { this.alvo = alvo; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public LocalDate getVigenciaInicio() { return vigenciaInicio; }
    public void setVigenciaInicio(LocalDate vigenciaInicio) { this.vigenciaInicio = vigenciaInicio; }

    public LocalDate getVigenciaFim() { return vigenciaFim; }
    public void setVigenciaFim(LocalDate vigenciaFim) { this.vigenciaFim = vigenciaFim; }

    public Integer getPrioridade() { return prioridade; }
    public void setPrioridade(Integer prioridade) { this.prioridade = prioridade; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public String getEscopo() { return escopo; }
    public void setEscopo(String escopo) { this.escopo = escopo; }
}

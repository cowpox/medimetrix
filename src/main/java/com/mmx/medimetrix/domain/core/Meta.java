package com.mmx.medimetrix.domain.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.META (V12__create_meta.sql)
 *
 * Observações:
 * - OPERADOR no DDL é VARCHAR(2) com valores: ">=", "<=", "=".
 *   Aqui mantemos como String para aderir 1:1 ao banco (o DAO pode validar/converter se desejar).
 */
public class Meta {

    private Long idMeta;                 // ID_META BIGSERIAL (PK)

    private Long idCriterio;             // NOT NULL (FK -> CRITERIO)
    private Long idUnidade;              // NULLABLE  (FK -> UNIDADE, adicionada no V15)
    private Long idEspecialidade;        // NULLABLE  (FK -> ESPECIALIDADE, adicionada no V15)

    private BigDecimal alvo;             // NUMERIC(5,2) NOT NULL
    private String operador;             // VARCHAR(2) NOT NULL ('>=','<=','=')

    private LocalDate vigenciaInicio;    // DATE
    private LocalDate vigenciaFim;       // DATE

    private Boolean ativo;               // DEFAULT TRUE
    private Integer prioridade;          // INT
    private String justificativa;        // VARCHAR(1000)

    private LocalDateTime dataCriacao;        // TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;   // TIMESTAMP NOT NULL

    public Meta() {}

    public Long getIdMeta() { return idMeta; }
    public void setIdMeta(Long idMeta) { this.idMeta = idMeta; }

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

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Integer getPrioridade() { return prioridade; }
    public void setPrioridade(Integer prioridade) { this.prioridade = prioridade; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "Meta{" +
                "idMeta=" + idMeta +
                ", idCriterio=" + idCriterio +
                ", idUnidade=" + idUnidade +
                ", idEspecialidade=" + idEspecialidade +
                ", alvo=" + alvo +
                ", operador='" + operador + '\'' +
                ", vigenciaInicio=" + vigenciaInicio +
                ", vigenciaFim=" + vigenciaFim +
                ", ativo=" + ativo +
                ", prioridade=" + prioridade +
                ", justificativa='" + justificativa + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

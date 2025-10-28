package com.mmx.medimetrix.domain.core;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO alinhado estritamente a MEDIMETRIX.QUESTAO (V2__create_questao.sql)
 *
 * Observações:
 * - TIPO é VARCHAR(12) com valores: 'LIKERT_5','BARS_5','OPEN','CHECK','NUM_0_10'.
 *   Mantemos como String para aderir 1:1 ao DDL.
 * - OBRIGATORIEDADE é VARCHAR(12): 'OPCIONAL' | 'OBRIGATORIA' (String aqui).
 * - VISIVEL_PARA_GESTOR é BOOLEAN (sem enum).
 */
public class Questao {

    private Long idQuestao;            // ID_QUESTAO BIGSERIAL (PK)
    private Long idCriterio;           // FK para CRITERIO (pode ser null até V3 criar FK)

    private String enunciado;          // VARCHAR(1000) NOT NULL
    private String descricaoAuxiliar;  // VARCHAR(500)

    private Boolean ativo;             // BOOLEAN NOT NULL DEFAULT TRUE
    private String tipo;               // VARCHAR(12) NOT NULL
    private String obrigatoriedade;    // VARCHAR(12) NOT NULL DEFAULT 'OBRIGATORIA'

    private BigDecimal validacaoNumMin; // NUMERIC(10,3)
    private BigDecimal validacaoNumMax; // NUMERIC(10,3)
    private Integer tamanhoTextoMax;    // INT

    private Boolean sensivel;           // BOOLEAN NOT NULL DEFAULT FALSE
    private Boolean visivelParaGestor;  // BOOLEAN NOT NULL DEFAULT TRUE

    private Integer versao;             // INT NOT NULL DEFAULT 1
    private Integer ordemSugerida;      // INT

    private LocalDateTime dataCriacao;       // TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;  // TIMESTAMP NOT NULL

    public Questao() {}

    public Long getIdQuestao() { return idQuestao; }
    public void setIdQuestao(Long idQuestao) { this.idQuestao = idQuestao; }

    public Long getIdCriterio() { return idCriterio; }
    public void setIdCriterio(Long idCriterio) { this.idCriterio = idCriterio; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getDescricaoAuxiliar() { return descricaoAuxiliar; }
    public void setDescricaoAuxiliar(String descricaoAuxiliar) { this.descricaoAuxiliar = descricaoAuxiliar; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

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

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }

    public Integer getOrdemSugerida() { return ordemSugerida; }
    public void setOrdemSugerida(Integer ordemSugerida) { this.ordemSugerida = ordemSugerida; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "Questao{" +
                "idQuestao=" + idQuestao +
                ", idCriterio=" + idCriterio +
                ", enunciado='" + enunciado + '\'' +
                ", ativo=" + ativo +
                ", tipo='" + tipo + '\'' +
                ", obrigatoriedade='" + obrigatoriedade + '\'' +
                ", validacaoNumMin=" + validacaoNumMin +
                ", validacaoNumMax=" + validacaoNumMax +
                ", tamanhoTextoMax=" + tamanhoTextoMax +
                ", sensivel=" + sensivel +
                ", visivelParaGestor=" + visivelParaGestor +
                ", versao=" + versao +
                ", ordemSugerida=" + ordemSugerida +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

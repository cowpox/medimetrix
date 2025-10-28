package com.mmx.medimetrix.domain.core;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.AVALIACAO_QUESTAO (V5__create_avaliacao_questao.sql)
 * PK composta: (ID_AVALIACAO, ID_QUESTAO)
 */
public class AvaliacaoQuestao {

    private Long idAvaliacao;                  // NOT NULL, FK -> AVALIACAO
    private Long idQuestao;                    // NOT NULL, FK -> QUESTAO

    private Integer ordem;                     // NOT NULL (>=1)
    private Boolean obrigatoriaNaAval;         // NULL = herda da QUESTAO
    private Boolean ativaNaAval;               // NOT NULL DEFAULT TRUE
    private BigDecimal pesoNaAval;             // NUMERIC(6,2) DEFAULT 1.00 (null ou > 0)
    private Boolean visivelParaGestorLocal;    // NULL = herda da QUESTAO
    private String observacaoAdmin;            // VARCHAR(200)

    private LocalDateTime dataCriacao;         // TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;    // TIMESTAMP NOT NULL

    public AvaliacaoQuestao() {}

    public Long getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Long idAvaliacao) { this.idAvaliacao = idAvaliacao; }

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

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "AvaliacaoQuestao{" +
                "idAvaliacao=" + idAvaliacao +
                ", idQuestao=" + idQuestao +
                ", ordem=" + ordem +
                ", obrigatoriaNaAval=" + obrigatoriaNaAval +
                ", ativaNaAval=" + ativaNaAval +
                ", pesoNaAval=" + pesoNaAval +
                ", visivelParaGestorLocal=" + visivelParaGestorLocal +
                ", observacaoAdmin='" + observacaoAdmin + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

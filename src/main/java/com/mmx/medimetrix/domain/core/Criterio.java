package com.mmx.medimetrix.domain.core;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.CRITERIO (V3__create_criterio.sql)
 */
public class Criterio {

    private Long idCriterio;                 // ID_CRITERIO BIGSERIAL (PK)
    private String nome;                     // NOME VARCHAR(100) NOT NULL (único, trim > 0)
    private String definicaoOperacional;     // DEFINICAO_OPERACIONAL VARCHAR(500)
    private String descricao;                // DESCRICAO VARCHAR(1000)

    private Boolean ativo;                   // ATIVO BOOLEAN DEFAULT TRUE

    private BigDecimal peso;                 // NUMERIC(6,2) DEFAULT 1.00 (null ou > 0)
    private Integer ordemSugerida;           // INT

    private Integer versao;                  // INT NOT NULL DEFAULT 1

    private LocalDateTime dataCriacao;       // TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;  // TIMESTAMP NOT NULL

    public Criterio() {}

    public Long getIdCriterio() { return idCriterio; }
    public void setIdCriterio(Long idCriterio) { this.idCriterio = idCriterio; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDefinicaoOperacional() { return definicaoOperacional; }
    public void setDefinicaoOperacional(String definicaoOperacional) { this.definicaoOperacional = definicaoOperacional; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getOrdemSugerida() { return ordemSugerida; }
    public void setOrdemSugerida(Integer ordemSugerida) { this.ordemSugerida = ordemSugerida; }

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriicao) { this.dataCriacao = dataCriicao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "Criterio{" +
                "idCriterio=" + idCriterio +
                ", nome='" + nome + '\'' +
                ", definicaoOperacional='" + definicaoOperacional + '\'' +
                ", descricao='" + descricao + '\'' +
                ", ativo=" + ativo +
                ", peso=" + peso +
                ", ordemSugerida=" + ordemSugerida +
                ", versao=" + versao +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

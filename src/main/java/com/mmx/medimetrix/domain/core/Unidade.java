package com.mmx.medimetrix.domain.core;

import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.UNIDADE (V6__create_unidade.sql)
 */
public class Unidade {

    private Long idUnidade;                 // ID_UNIDADE BIGSERIAL (PK)
    private String nome;                    // NOME VARCHAR(120) NOT NULL (trim > 0)
    private Long gestorUsuarioId;           // GESTOR_USUARIO_ID BIGINT NOT NULL (FK para USUARIO)
    private Boolean ativo;                  // ATIVO BOOLEAN NOT NULL DEFAULT TRUE

    private LocalDateTime dataCriacao;          // DATA_CRIACAO TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;     // DATA_ULTIMA_EDICAO TIMESTAMP NOT NULL

    public Unidade() {}

    public Long getIdUnidade() { return idUnidade; }
    public void setIdUnidade(Long idUnidade) { this.idUnidade = idUnidade; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Long getGestorUsuarioId() { return gestorUsuarioId; }
    public void setGestorUsuarioId(Long gestorUsuarioId) { this.gestorUsuarioId = gestorUsuarioId; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "Unidade{" +
                "idUnidade=" + idUnidade +
                ", nome='" + nome + '\'' +
                ", gestorUsuarioId=" + gestorUsuarioId +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

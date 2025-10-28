package com.mmx.medimetrix.domain.core;

import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.ESPECIALIDADE (V7__create_especialidade.sql)
 */
public class Especialidade {

    private Long idEspecialidade;          // ID_ESPECIALIDADE BIGSERIAL (PK)
    private String nome;                   // NOME VARCHAR(120) NOT NULL (trim > 0)
    private Boolean ativo;                 // ATIVO BOOLEAN NOT NULL DEFAULT TRUE

    private LocalDateTime dataCriacao;         // DATA_CRIACAO TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;    // DATA_ULTIMA_EDICAO TIMESTAMP NOT NULL

    public Especialidade() {}

    public Long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "Especialidade{" +
                "idEspecialidade=" + idEspecialidade +
                ", nome='" + nome + '\'' +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

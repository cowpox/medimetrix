package com.mmx.medimetrix.domain.core;

import com.mmx.medimetrix.domain.enums.Papel;
import java.time.LocalDateTime;

/**
 * POJO alinhado ao DDL V8__create_usuario.sql
 * Tabela: MEDIMETRIX.USUARIO
 */
public class Usuario {

    private Long idUsuario;              // ID_USUARIO BIGSERIAL (PK)
    private String nome;                 // NOME VARCHAR(120) NOT NULL
    private String email;                // EMAIL VARCHAR(255) NOT NULL (único case-insensitive)
    private Boolean ativo;               // ATIVO BOOLEAN NOT NULL DEFAULT TRUE
    private Papel papel;                 // PAPEL VARCHAR(10) NOT NULL (MEDICO|GESTOR|ADMIN)

    private LocalDateTime dataCriacao;        // DATA_CRIACAO TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;   // DATA_ULTIMA_EDICAO TIMESTAMP NOT NULL

    public Usuario() {}

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Papel getPapel() { return papel; }
    public void setPapel(Papel papel) { this.papel = papel; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", ativo=" + ativo +
                ", papel=" + papel +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

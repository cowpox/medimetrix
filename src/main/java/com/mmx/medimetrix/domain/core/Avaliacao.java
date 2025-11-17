package com.mmx.medimetrix.domain.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.AVALIACAO (V4__create_avaliacao.sql)
 *
 * STATUS é VARCHAR(12) com valores: 'RASCUNHO', 'PUBLICADA', 'ENCERRADA'.
 * Mantido como String para aderência 1:1 ao DDL.
 */
public class Avaliacao {

    private Long idAvaliacao;            // ID_AVALIACAO BIGSERIAL (PK)
    private String titulo;               // TITULO VARCHAR(120) NOT NULL

    private LocalDate dataInicioAplic;   // DATE NOT NULL
    private LocalDate dataFimAplic;      // DATE NOT NULL

    private Integer kMinimo;             // INT NOT NULL DEFAULT 3
    private String status;               // VARCHAR(12) NOT NULL
    private Boolean ativo;               // BOOLEAN NOT NULL DEFAULT TRUE

    private Integer versao;              // INT NOT NULL DEFAULT 1

    private LocalDateTime dataCriacao;       // TIMESTAMP NOT NULL
    private LocalDateTime dataUltimaEdicao;  // TIMESTAMP NOT NULL

    private String escopo;
    private Long idUnidade;
    private Long idEspecialidade;

    public Avaliacao() {}

    public Long getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Long idAvaliacao) { this.idAvaliacao = idAvaliacao; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getDataInicioAplic() { return dataInicioAplic; }
    public void setDataInicioAplic(LocalDate dataInicioAplic) { this.dataInicioAplic = dataInicioAplic; }

    public LocalDate getDataFimAplic() { return dataFimAplic; }
    public void setDataFimAplic(LocalDate dataFimAplic) { this.dataFimAplic = dataFimAplic; }

    public Integer getkMinimo() { return kMinimo; }
    public void setkMinimo(Integer kMinimo) { this.kMinimo = kMinimo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    public String getEscopo() { return escopo; }
    public void setEscopo(String escopo) { this.escopo = escopo; }

    public Long getIdUnidade() { return idUnidade; }
    public void setIdUnidade(Long idUnidade) { this.idUnidade = idUnidade; }

    public Long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    @Override
    public String toString() {
        return "Avaliacao{" +
                "idAvaliacao=" + idAvaliacao +
                ", titulo='" + titulo + '\'' +
                ", dataInicioAplic=" + dataInicioAplic +
                ", dataFimAplic=" + dataFimAplic +
                ", kMinimo=" + kMinimo +
                ", status='" + status + '\'' +
                ", ativo=" + ativo +
                ", versao=" + versao +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

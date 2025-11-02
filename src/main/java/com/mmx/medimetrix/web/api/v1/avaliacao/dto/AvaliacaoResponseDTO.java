package com.mmx.medimetrix.web.api.v1.avaliacao.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AvaliacaoResponseDTO {
    private Long idAvaliacao;
    private String titulo;
    private LocalDate dataInicioAplic;
    private LocalDate dataFimAplic;
    private Integer kMinimo;
    private String status;
    private Boolean ativo;
    private Integer versao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaEdicao;

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
}

package com.mmx.medimetrix.application.avaliacao.commands;

import java.time.LocalDate;

public class AvaliacaoCreate {
    private String titulo;
    private LocalDate dataInicioAplic;
    private LocalDate dataFimAplic;
    private Integer kMinimo;
    private String status; // RASCUNHO | PUBLICADA | ENCERRADA
    private Boolean ativo;

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
}

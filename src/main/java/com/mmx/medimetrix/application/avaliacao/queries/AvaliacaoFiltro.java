package com.mmx.medimetrix.application.avaliacao.queries;

import java.time.LocalDate;

public class AvaliacaoFiltro {
    private String tituloLike;     // opcional (ILIKE %...%)
    private String status;         // RASCUNHO | PUBLICADA | ENCERRADA
    private Boolean ativo;         // true/false
    private LocalDate naData;      // filtra avaliações vigentes na data (inicio<=naData<=fim(NULL ok))

    public String getTituloLike() { return tituloLike; }
    public void setTituloLike(String tituloLike) { this.tituloLike = tituloLike; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDate getNaData() { return naData; }
    public void setNaData(LocalDate naData) { this.naData = naData; }
}

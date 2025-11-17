package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.domain.core.Avaliacao;

import java.time.format.DateTimeFormatter;

public class AvaliacaoListItemVM {

    private Long id;
    private String titulo;
    private String periodo;        // ex.: "01/01/2025 – 31/01/2025"
    private String status;         // RASCUNHO / PUBLICADA / ENCERRADA
    private Integer totalQuestoes; // contagem vinda de AvaliacaoQuestaoService

    // --- Getters / Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalQuestoes() {
        return totalQuestoes;
    }

    public void setTotalQuestoes(Integer totalQuestoes) {
        this.totalQuestoes = totalQuestoes;
    }

    // --- Factory helper ---

    public static AvaliacaoListItemVM from(Avaliacao a, int totalQuestoes) {
        var fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        AvaliacaoListItemVM vm = new AvaliacaoListItemVM();
        vm.setId(a.getIdAvaliacao());
        vm.setTitulo(a.getTitulo());

        if (a.getDataInicioAplic() != null && a.getDataFimAplic() != null) {
            String periodo = a.getDataInicioAplic().format(fmt)
                    + " – "
                    + a.getDataFimAplic().format(fmt);
            vm.setPeriodo(periodo);
        } else {
            vm.setPeriodo("—");
        }

        vm.setStatus(a.getStatus());
        vm.setTotalQuestoes(totalQuestoes);

        return vm;
    }
}

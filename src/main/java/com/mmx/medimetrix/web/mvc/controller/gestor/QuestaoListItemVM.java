package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.domain.core.Questao;

public record QuestaoListItemVM(
        Long id,
        String enunciado,
        String criterioNome,
        String tipoLabel,
        boolean sensivel,
        boolean visivelParaGestor,
        boolean ativo
) {

    public static QuestaoListItemVM from(Questao q, String criterioNome) {
        String tipo = q.getTipo();
        String tipoLabel;

        if ("LIKERT_5".equals(tipo)) {
            tipoLabel = "Likert 1–5";
        } else if ("BARS_5".equals(tipo)) {
            tipoLabel = "BARS 1–5";
        } else if ("OPEN".equals(tipo)) {
            tipoLabel = "Texto (OPEN)";
        } else if ("CHECK".equals(tipo)) {
            tipoLabel = "Binário (CHECK)";
        } else if ("NUM_0_10".equals(tipo)) {
            tipoLabel = "Numérico 0–10";
        } else {
            tipoLabel = tipo != null ? tipo : "-";
        }

        boolean sensivel = Boolean.TRUE.equals(q.getSensivel());
        boolean visivelParaGestor = Boolean.TRUE.equals(q.getVisivelParaGestor());
        boolean ativo = Boolean.TRUE.equals(q.getAtivo());

        return new QuestaoListItemVM(
                q.getIdQuestao(),
                q.getEnunciado(),
                criterioNome,
                tipoLabel,
                sensivel,
                visivelParaGestor,
                ativo
        );
    }
}

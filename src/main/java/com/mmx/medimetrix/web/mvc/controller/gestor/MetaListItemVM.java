package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.domain.core.Meta;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

public record MetaListItemVM(
        Long id,
        String metaLabel,
        String criterioNome,
        String vigenciaLabel,
        String escopoLabel,
        String prioridadeLabel,
        String prioridadeBadgeClass,
        boolean ativo
) {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static MetaListItemVM from(Meta m, String criterioNome) {

        // 1) Escopo
        String escopo;
        if (m.getIdUnidade() != null && m.getIdEspecialidade() == null) {
            escopo = "Unidade";
        } else if (m.getIdEspecialidade() != null) {
            escopo = "Especialidade";
        } else {
            escopo = "Global";
        }

        // 2) Meta (Média + escopo + operador + alvo)
        String alvoFmt = m.getAlvo() == null
                ? "-"
                : m.getAlvo().setScale(2, RoundingMode.HALF_UP)
                .toString()
                .replace('.', ','); // 4.40 -> 4,40

        String metaLabel = "Média " + escopo + " " + m.getOperador() + " " + alvoFmt;

        // 3) Vigência
        String vigencia;
        if (m.getVigenciaInicio() == null && m.getVigenciaFim() == null) {
            vigencia = "-";
        } else if (m.getVigenciaInicio() != null && m.getVigenciaFim() == null) {
            vigencia = m.getVigenciaInicio().format(DF) + " em diante";
        } else if (m.getVigenciaInicio() == null) {
            vigencia = "Até " + m.getVigenciaFim().format(DF);
        } else {
            vigencia = m.getVigenciaInicio().format(DF) + " – " + m.getVigenciaFim().format(DF);
        }

        // 4) Prioridade (assumindo 1 = Alta, 2 = Média, 3 = Baixa)
        String priLabel;
        String priClass;
        if (m.getPrioridade() == null) {
            priLabel = "-";
            priClass = "text-bg-secondary";
        } else if (m.getPrioridade() == 1) {
            priLabel = "Alta";
            priClass = "text-bg-danger";
        } else if (m.getPrioridade() == 2) {
            priLabel = "Média";
            priClass = "text-bg-warning text-dark";
        } else {
            priLabel = "Baixa";
            priClass = "text-bg-secondary";
        }

        boolean ativo = Boolean.TRUE.equals(m.getAtivo());

        return new MetaListItemVM(
                m.getIdMeta(),
                metaLabel,
                criterioNome,
                vigencia,
                escopo,
                priLabel,
                priClass,
                ativo
        );
    }
}

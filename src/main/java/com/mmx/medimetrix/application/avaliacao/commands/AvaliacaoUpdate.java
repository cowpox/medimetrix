package com.mmx.medimetrix.application.avaliacao.commands;

import java.time.LocalDate;

// record com TODOS os campos, inclusive os novos
public record AvaliacaoUpdate(
        String titulo,
        LocalDate dataInicioAplic,
        LocalDate dataFimAplic,
        Integer kMinimo,
        String status,
        Boolean ativo,
        String escopo,        // GLOBAL | UNIDADE | ESPECIALIDADE
        Long idUnidade,
        Long idEspecialidade
) {

    // Construtor "legado" (usado pela API v1), mantendo a mesma assinatura antiga
    // Comporta-se como antes: escopo GLOBAL, sem unidade/especialidade.
    public AvaliacaoUpdate(String titulo,
                           LocalDate dataInicioAplic,
                           LocalDate dataFimAplic,
                           Integer kMinimo,
                           String status,
                           Boolean ativo) {
        this(
                titulo,
                dataInicioAplic,
                dataFimAplic,
                kMinimo,
                status,
                ativo,
                "GLOBAL",   // escopo padrão
                null,       // idUnidade
                null        // idEspecialidade
        );
    }
}

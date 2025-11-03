package com.mmx.medimetrix.application.avaliacao.commands;

import java.time.LocalDate;

public record AvaliacaoUpdate(
        String titulo,
        LocalDate dataInicioAplic,
        LocalDate dataFimAplic,
        Integer kMinimo,
        String status,     // RASCUNHO | PUBLICADA | ENCERRADA
        Boolean ativo
) {}

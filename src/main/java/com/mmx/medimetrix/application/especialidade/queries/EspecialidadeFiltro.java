package com.mmx.medimetrix.application.especialidade.queries;

public record EspecialidadeFiltro(
        String nomeLike,
        Integer page,
        Integer size,
        String sortBy,
        Boolean asc,
        Boolean ativo // <= NOVO
) {}

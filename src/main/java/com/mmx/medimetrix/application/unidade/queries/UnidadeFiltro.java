package com.mmx.medimetrix.application.unidade.queries;

public record UnidadeFiltro(
        String nomeLike,
        Integer page,
        Integer size,
        String sortBy,
        Boolean asc
) {}

package com.mmx.medimetrix.application.meta.queries;

import java.time.LocalDate;

public record MetaFiltro(Long idCriterio, Long idUnidade, Long idEspecialidade,
                         LocalDate naData, Integer page, Integer size) {}

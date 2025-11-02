package com.mmx.medimetrix.application.meta.service;

import com.mmx.medimetrix.application.meta.commands.MetaCreate;
import com.mmx.medimetrix.application.meta.queries.MetaFiltro;
import com.mmx.medimetrix.domain.core.Meta;

import java.util.List;
import java.util.Optional;

public interface MetaService {
    Meta create(MetaCreate cmd);
    Meta update(Long id, Long idCriterio, Long idUnidade, Long idEspecialidade,
                java.math.BigDecimal alvo, String operador,
                java.time.LocalDate vigenciaInicio, java.time.LocalDate vigenciaFim,
                Boolean ativo, Integer prioridade, String justificativa);
    Optional<Meta> findById(Long id);
    List<Meta> list(MetaFiltro filtro);
    void activate(Long id);
    void deactivate(Long id);
}

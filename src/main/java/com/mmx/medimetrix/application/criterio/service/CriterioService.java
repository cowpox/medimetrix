package com.mmx.medimetrix.application.criterio.service;

import com.mmx.medimetrix.application.criterio.commands.CriterioCreate;
import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.domain.core.Criterio;

import java.util.List;
import java.util.Optional;

public interface CriterioService {
    Criterio create(CriterioCreate cmd);
    Criterio update(Long id, String nome, String definicaoOperacional, String descricao, Boolean ativo);
    Optional<Criterio> findById(Long id);
    List<Criterio> list(CriterioFiltro filtro);
    void activate(Long id);
    void deactivate(Long id);
}

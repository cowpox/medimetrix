package com.mmx.medimetrix.application.unidade.service;

import com.mmx.medimetrix.application.unidade.commands.UnidadeCreate;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.domain.core.Unidade;

import java.util.List;
import java.util.Optional;

public interface UnidadeService {
    Unidade create(UnidadeCreate cmd);
    Unidade update(Long id, String nome, Boolean ativo);
    Optional<Unidade> findById(Long id);
    List<Unidade> list(UnidadeFiltro filtro);
    void deactivate(Long id);
    void activate(Long id);
}

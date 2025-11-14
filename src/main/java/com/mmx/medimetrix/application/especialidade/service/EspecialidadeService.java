package com.mmx.medimetrix.application.especialidade.service;

import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeCreate;
import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeUpdate;
import com.mmx.medimetrix.application.especialidade.queries.EspecialidadeFiltro;
import com.mmx.medimetrix.domain.core.Especialidade;

import java.util.List;
import java.util.Optional;

public interface EspecialidadeService {
    Especialidade create(EspecialidadeCreate cmd);
    Especialidade update(Long id, EspecialidadeUpdate cmd);
    Optional<Especialidade> findById(Long id);
    List<Especialidade> list(EspecialidadeFiltro filtro);
    List<Especialidade> listAtivas();
    void deactivate(Long id);
    void activate(Long id);
}

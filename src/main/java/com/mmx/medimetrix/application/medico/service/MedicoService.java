package com.mmx.medimetrix.application.medico.service;

import com.mmx.medimetrix.application.medico.commands.MedicoCreate;
import com.mmx.medimetrix.application.medico.commands.MedicoUpdate;
import com.mmx.medimetrix.domain.core.Medico;

import java.util.List;
import java.util.Optional;

public interface MedicoService {
    Long create(MedicoCreate cmd);                         // retorna usuarioId
    void update(Long usuarioId, MedicoUpdate cmd);
    Optional<Medico> findByUsuarioId(Long usuarioId);
    Optional<Medico> findByCrm(String crmNumero, String crmUf);

    List<Medico> listByEspecialidade(Long especialidadeId, int page, int size);
    List<Medico> listByUnidade(Long unidadeId, int page, int size);
    List<Medico> listPaged(int page, int size);

    void deleteByUsuarioId(Long usuarioId);
}

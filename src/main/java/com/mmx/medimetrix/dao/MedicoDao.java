package com.mmx.medimetrix.dao;

import com.mmx.medimetrix.domain.core.Medico;
import java.util.List;
import java.util.Optional;

public interface MedicoDao {
    Long insert(Medico m); // retorna USUARIO_ID
    int update(Medico m);
    Optional<Medico> findByUsuarioId(Long usuarioId);
    Optional<Medico> findByCrm(String crmNumero, String crmUf);
    List<Medico> listByEspecialidade(Long especialidadeId, Integer offset, Integer limit);
    List<Medico> listByUnidade(Long unidadeId, Integer offset, Integer limit);
    List<Medico> listPaged(Integer offset, Integer limit);
    int deleteByUsuarioId(Long usuarioId);
}

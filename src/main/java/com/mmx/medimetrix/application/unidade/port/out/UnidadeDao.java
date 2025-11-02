package com.mmx.medimetrix.application.unidade.port.out;

import com.mmx.medimetrix.domain.core.Unidade;
import java.util.List;
import java.util.Optional;

public interface UnidadeDao {
    Long insert(Unidade u);
    int update(Unidade u);
    Optional<Unidade> findById(Long id);
    List<Unidade> listAllActive();
    List<Unidade> listByGestor(Long gestorUsuarioId);
    List<Unidade> listPaged(Integer offset, Integer limit);
    List<Unidade> searchByNomeLikePaged(String termo, Integer offset, Integer limit);
    int deactivate(Long id);
    int reactivate(Long id);
    int deleteById(Long id);
}

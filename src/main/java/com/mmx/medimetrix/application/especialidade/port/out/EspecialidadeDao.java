package com.mmx.medimetrix.application.especialidade.port.out;

import com.mmx.medimetrix.domain.core.Especialidade;
import java.util.List;
import java.util.Optional;

public interface EspecialidadeDao {
    Long insert(Especialidade e);
    int update(Especialidade e);
    Optional<Especialidade> findById(Long id);
    List<Especialidade> listAllActive();
    List<Especialidade> listPaged(Integer offset, Integer limit);
    List<Especialidade> searchByNomeLikePaged(String termo, Integer offset, Integer limit);

    // ===== NOVOS para suportar filtro por ativo =====
    List<Especialidade> listByAtivoPaged(Boolean ativo, Integer offset, Integer limit);
    List<Especialidade> searchByNomeAndAtivoLikePaged(String termo, Boolean ativo, Integer offset, Integer limit);

    List<Especialidade> listPagedOrdered(String sortBy, boolean asc, Integer offset, Integer limit);
    List<Especialidade> searchByNomeLikePagedOrdered(String termo, String sortBy, boolean asc, Integer offset, Integer limit);
    List<Especialidade> listByAtivoPagedOrdered(Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit);
    List<Especialidade> searchByNomeAndAtivoLikePagedOrdered(String termo, Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit);


    int deactivate(Long id);
    int reactivate(Long id);
    int deleteById(Long id);
}

package com.mmx.medimetrix.dao;

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
    int deactivate(Long id);
    int reactivate(Long id);
    int deleteById(Long id);
}

package com.mmx.medimetrix.dao;

import com.mmx.medimetrix.domain.core.Criterio;
import java.util.List;
import java.util.Optional;

public interface CriterioDao {
    Long insert(Criterio c);
    int update(Criterio c);
    Optional<Criterio> findById(Long id);
    Optional<Criterio> findByNomeCaseInsensitive(String nome);
    List<Criterio> listAllActive();
    List<Criterio> listPaged(Integer offset, Integer limit);
    List<Criterio> searchByNomeLikePaged(String termo, Integer offset, Integer limit);
    int deactivate(Long id);
    int reactivate(Long id);
    int deleteById(Long id);
}

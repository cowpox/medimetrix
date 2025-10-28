package com.mmx.medimetrix.dao;

import com.mmx.medimetrix.domain.core.Avaliacao;
import java.util.List;
import java.util.Optional;

public interface AvaliacaoDao {
    Long insert(Avaliacao a);
    int update(Avaliacao a);
    Optional<Avaliacao> findById(Long id);
    List<Avaliacao> listByStatus(String status, Integer offset, Integer limit);
    List<Avaliacao> listPaged(Integer offset, Integer limit);
    List<Avaliacao> searchByTituloLikePaged(String termo, Integer offset, Integer limit);
    int deactivate(Long id);
    int reactivate(Long id);
    int deleteById(Long id);
}

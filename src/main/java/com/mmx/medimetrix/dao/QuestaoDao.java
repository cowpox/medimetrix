package com.mmx.medimetrix.dao;

import com.mmx.medimetrix.domain.core.Questao;
import java.util.List;
import java.util.Optional;

public interface QuestaoDao {
    Long insert(Questao q);
    int update(Questao q);
    Optional<Questao> findById(Long id);
    List<Questao> listPaged(Integer offset, Integer limit);
    List<Questao> listAtivasByTipo(String tipo, Integer offset, Integer limit);
    List<Questao> listByCriterio(Long idCriterio, Integer offset, Integer limit);
    List<Questao> searchByEnunciadoLikePaged(String termo, Integer offset, Integer limit);
    int deactivate(Long id);
    int reactivate(Long id);
    int deleteById(Long id);
}

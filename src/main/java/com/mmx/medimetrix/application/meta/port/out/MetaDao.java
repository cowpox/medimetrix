package com.mmx.medimetrix.application.meta.port.out;

import com.mmx.medimetrix.domain.core.Meta;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MetaDao {
    Long insert(Meta m);
    int update(Meta m);
    Optional<Meta> findById(Long id);
    List<Meta> listByCriterio(Long idCriterio, Integer offset, Integer limit);
    List<Meta> listByEscopo(Long idCriterio, Long idUnidade, Long idEspecialidade, Integer offset, Integer limit);
    List<Meta> listAtivasVigentesEm(LocalDate data, Integer offset, Integer limit);
    int deactivate(Long id);
    int reactivate(Long id);
    int deleteById(Long id);
}

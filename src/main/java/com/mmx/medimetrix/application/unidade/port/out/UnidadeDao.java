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

    // existentes
    List<Unidade> listPaged(Integer offset, Integer limit);
    List<Unidade> searchByNomeLikePaged(String termo, Integer offset, Integer limit);

    // novos (filtro por ativo)
    List<Unidade> listByAtivoPaged(Boolean ativo, Integer offset, Integer limit);
    List<Unidade> searchByNomeAndAtivoLikePaged(String termo, Boolean ativo, Integer offset, Integer limit);

    // novos (com ORDER BY dinâmico e seguro)
    List<Unidade> listPagedOrdered(String sortBy, boolean asc, Integer offset, Integer limit);
    List<Unidade> searchByNomeLikePagedOrdered(String termo, String sortBy, boolean asc, Integer offset, Integer limit);
    List<Unidade> listByAtivoPagedOrdered(Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit);
    List<Unidade> searchByNomeAndAtivoLikePagedOrdered(String termo, Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit);

    // busca por termo em UNIDADE.NOME OU USUARIO.NOME (com ORDER BY dinâmico)
    List<Unidade> searchByNomeOuGestorLikePagedOrdered(String termo, String sortBy, boolean asc,
                                                       Integer offset, Integer limit);

    List<Unidade> searchByNomeOuGestorAndAtivoLikePagedOrdered(String termo, Boolean ativo, String sortBy, boolean asc,
                                                               Integer offset, Integer limit);


    int deactivate(Long id);
    int reactivate(Long id);

    // manter, mas não usar na UI admin (sem hard delete)
    int deleteById(Long id);
}

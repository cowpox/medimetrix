package com.mmx.medimetrix.application.usuario.port.out;

import com.mmx.medimetrix.domain.core.Usuario;
import com.mmx.medimetrix.domain.enums.Papel;
import java.util.List;
import java.util.Optional;



public interface UsuarioDao {
    Long insert(Usuario u);
    int update(Usuario u);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmailCaseInsensitive(String email);
    List<Usuario> listPaged(Integer offset, Integer limit);
    List<Usuario> listByPapelPaged(String papel, Integer offset, Integer limit);
    List<Usuario> listByPapelAtivo(Papel papel, boolean ativo);
    int deactivate(Long id);  // ATIVO = FALSE
    int reactivate(Long id);  // ATIVO = TRUE
    int deleteById(Long id);

    // busca com filtros + ordenação + paginação
    List<Usuario> search(String termo, Papel papel, Boolean ativo,
                         int page, int size, String sort, boolean asc);
}

package com.mmx.medimetrix.application.usuario.port.out;

import com.mmx.medimetrix.domain.core.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDao {
    Long insert(Usuario u);
    int update(Usuario u);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmailCaseInsensitive(String email);
    List<Usuario> listPaged(Integer offset, Integer limit);
    List<Usuario> listByPapelPaged(String papel, Integer offset, Integer limit);
    int deactivate(Long id);  // ATIVO = FALSE
    int reactivate(Long id);  // ATIVO = TRUE
    int deleteById(Long id);
}

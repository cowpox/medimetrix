package com.mmx.medimetrix.application.usuario.service;

import com.mmx.medimetrix.application.usuario.commands.UsuarioCreate;
import com.mmx.medimetrix.application.usuario.commands.UsuarioUpdate;
import com.mmx.medimetrix.application.usuario.queries.UsuarioFiltro;
import com.mmx.medimetrix.domain.core.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario create(UsuarioCreate cmd);
    Usuario getById(long id);
    List<Usuario> search(UsuarioFiltro filtro, int page, int size, String sortBy, boolean asc);
    void update(UsuarioUpdate cmd);
    void desativar(long id);
    void reativar(long id);
    void delete(long id);
}

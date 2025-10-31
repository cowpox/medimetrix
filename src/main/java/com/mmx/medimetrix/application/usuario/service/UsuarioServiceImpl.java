package com.mmx.medimetrix.application.usuario.service;

import com.mmx.medimetrix.application.usuario.commands.UsuarioCreate;
import com.mmx.medimetrix.application.usuario.exceptions.EmailDuplicadoException;
import com.mmx.medimetrix.application.usuario.exceptions.EntidadeNaoEncontradaException;
import com.mmx.medimetrix.application.usuario.port.out.UsuarioDao;
import com.mmx.medimetrix.application.usuario.queries.UsuarioFiltro;
import com.mmx.medimetrix.domain.core.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDao usuarioDao;

    public UsuarioServiceImpl(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    @Transactional
    public Usuario create(UsuarioCreate cmd) {
        // usa a assinatura existente no DAO
        if (usuarioDao.findByEmailCaseInsensitive(cmd.email()).isPresent()) {
            throw new EmailDuplicadoException(cmd.email());
        }

        Usuario novo = new Usuario();
        novo.setNome(cmd.nome().trim());
        novo.setEmail(cmd.email().trim());

        // se o seu POJO usa enum:
        novo.setPapel(cmd.papel());
        // se for String no POJO, troque para:
        // novo.setPapel(cmd.papel().name());

        // se não houver setter de 'ativo', deixe o DEFAULT TRUE do BD
        try {
            novo.setAtivo(true); // remova se seu POJO não tiver esse setter
        } catch (NoSuchMethodError | Exception ignored) {}

        Long id = usuarioDao.insert(novo);
        return usuarioDao.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario", id));
    }

    @Override
    public Usuario getById(long id) {
        return usuarioDao.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario", id));
    }

    @Override
    public List<Usuario> search(UsuarioFiltro filtro, int page, int size, String sortBy, boolean asc) {
        // Com o DAO atual não temos 'search' genérico; vamos delegar:
        int limit = Math.max(1, size);
        int offset = Math.max(0, page) * limit;

        if (filtro != null && filtro.papel() != null) {
            // converte enum -> String se seu DAO espera String
            return usuarioDao.listByPapelPaged(filtro.papel().name(), offset, limit);
        }
        // por ora, ignoramos 'termo' e 'ativo' até evoluir o DAO
        return usuarioDao.listPaged(offset, limit);
    }

    @Override
    @Transactional
    public void desativar(long id) {
        // garante 404 coerente
        getById(id);
        int rows = usuarioDao.deactivate(id);
        if (rows == 0) throw new EntidadeNaoEncontradaException("Usuario", id);
    }

    @Override
    @Transactional
    public void reativar(long id) {
        getById(id);
        int rows = usuarioDao.reactivate(id);
        if (rows == 0) throw new EntidadeNaoEncontradaException("Usuario", id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        int rows = usuarioDao.deleteById(id);
        if (rows == 0) throw new EntidadeNaoEncontradaException("Usuario", id);
    }
}

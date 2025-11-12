package com.mmx.medimetrix.application.usuario.service;

import com.mmx.medimetrix.application.usuario.commands.UsuarioCreate;
import com.mmx.medimetrix.application.usuario.commands.UsuarioUpdate;
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
        // evita duplicidade de e-mail
        if (usuarioDao.findByEmailCaseInsensitive(cmd.email()).isPresent()) {
            throw new EmailDuplicadoException(cmd.email());
        }

        Usuario novo = new Usuario();
        novo.setNome(cmd.nome().trim());
        novo.setEmail(cmd.email().trim());
        novo.setPapel(cmd.papel());
        novo.setAtivo(true); // default coerente com DDL

        Long id = usuarioDao.insert(novo);
        return usuarioDao.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario", id));
    }

    @Override
    @Transactional
    public void update(UsuarioUpdate cmd) {
        Usuario atual = usuarioDao.findById(cmd.idUsuario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario", cmd.idUsuario()));

        // verifica duplicidade de e-mail se mudou
        String novoEmail = cmd.email() != null ? cmd.email().trim() : null;
        if (novoEmail != null && !novoEmail.equalsIgnoreCase(atual.getEmail())) {
            usuarioDao.findByEmailCaseInsensitive(novoEmail).ifPresent(existing -> {
                if (!existing.getIdUsuario().equals(cmd.idUsuario())) {
                    throw new EmailDuplicadoException(novoEmail);
                }
            });
            atual.setEmail(novoEmail);
        }

        if (cmd.nome() != null) {
            atual.setNome(cmd.nome().trim());
        }
        if (cmd.papel() != null) {
            atual.setPapel(cmd.papel());
        }
        if (cmd.ativo() != null) {
            atual.setAtivo(cmd.ativo());
        }

        int rows = usuarioDao.update(atual);
        if (rows == 0) {
            throw new IllegalStateException("Falha ao atualizar usuário: " + cmd.idUsuario());
        }
    }

    @Override
    public Usuario getById(long id) {
        return usuarioDao.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario", id));
    }

    @Override
    public List<Usuario> search(UsuarioFiltro filtro, int page, int size, String sortBy, boolean asc) {
        int limit = Math.max(1, size);
        int offset = Math.max(0, page) * limit;

        // Garante que o filtro não é nulo
        if (filtro == null) {
            filtro = new UsuarioFiltro(null, null, null);
        }

        // Usa o novo DAO que trata termo, papel e ativo
        return usuarioDao.search(
                filtro.termo(),
                filtro.papel(),
                filtro.ativo(),
                page,
                size,
                sortBy,
                asc
        );
    }


    @Override
    @Transactional
    public void desativar(long id) {
        getById(id); // garante 404 coerente
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

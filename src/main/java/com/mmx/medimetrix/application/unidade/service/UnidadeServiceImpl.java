package com.mmx.medimetrix.application.unidade.service;

import com.mmx.medimetrix.application.unidade.commands.UnidadeCreate;
import com.mmx.medimetrix.application.unidade.commands.UnidadeUpdate;
import com.mmx.medimetrix.application.unidade.port.out.UnidadeDao;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.domain.core.Unidade;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.mmx.medimetrix.domain.enums.Papel;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UnidadeServiceImpl implements UnidadeService {

    private final UnidadeDao unidadeDao;
    private final UsuarioService usuarioService; // <— injete o service do usuário

    public UnidadeServiceImpl(UnidadeDao unidadeDao, UsuarioService usuarioService) {
        this.unidadeDao = unidadeDao;
        this.usuarioService = usuarioService;
    }
    @Override
    public Unidade create(UnidadeCreate cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("Payload obrigatório.");
        }
        if (!StringUtils.hasText(cmd.nome())) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        final String nome = cmd.nome().trim();
        if (nome.length() > 120) {
            throw new IllegalArgumentException("Nome deve ter no máximo 120 caracteres.");
        }

        // gestorUsuarioId obrigatório
        if (cmd.gestorUsuarioId() == null || cmd.gestorUsuarioId() <= 0) {
            throw new IllegalArgumentException("gestorUsuarioId é obrigatório.");
        }

        // (Recomendado) Validar existência e papel do gestor
        // Depende de ter UsuarioService (ou UsuarioDao) injetado aqui.
        var gestor = usuarioService.getById(cmd.gestorUsuarioId()); // se não existir, seu handler retorna 404
        if (gestor.getPapel() != Papel.GESTOR) {
            // 409 via ProblemDetailsHandler (IllegalStateException -> Conflict)
            throw new IllegalStateException("gestorUsuarioId precisa apontar para um usuário com papel GESTOR.");
        }

        // Montagem da unidade para inserção
        Unidade nova = new Unidade();
        nova.setNome(nome);
        nova.setGestorUsuarioId(cmd.gestorUsuarioId());
        nova.setAtivo(true);

        Long id = unidadeDao.insert(nova);
        return unidadeDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Unidade não encontrada após inserir."));
    }


    @Override
    public int update(UnidadeUpdate cmd) {
        if (cmd == null || cmd.id() == null) {
            throw new IllegalArgumentException("ID da unidade é obrigatório para atualização.");
        }

        Unidade unidade = unidadeDao.findById(cmd.id())
                .orElseThrow(() -> new NoSuchElementException("Unidade não encontrada: " + cmd.id()));

        if (cmd.nome() != null && !cmd.nome().trim().isEmpty()) {
            unidade.setNome(cmd.nome().trim());
        }
        if (cmd.ativo() != null) {
            unidade.setAtivo(cmd.ativo());
        }
        if (cmd.gestorUsuarioId() != null) {
            unidade.setGestorUsuarioId(cmd.gestorUsuarioId());
        }

        unidade.setDataUltimaEdicao(java.time.LocalDateTime.now());
        return unidadeDao.update(unidade);
    }

    @Override
    public Optional<Unidade> findById(Long id) {
        return unidadeDao.findById(id);
    }

    @Override
    public List<Unidade> list(UnidadeFiltro filtro) {
        int page = (filtro == null || filtro.page() == null || filtro.page() < 0) ? 0 : filtro.page();
        int size = (filtro == null || filtro.size() == null || filtro.size() < 1 || filtro.size() > 100) ? 20 : filtro.size();
        int offset = page * size;
        int limit  = size;

        String nomeLike = (filtro == null) ? null : filtro.nomeLike();
        Boolean ativo   = (filtro == null) ? null : filtro.ativo();

        String sortBy = (filtro == null || filtro.sortBy() == null) ? "nome" : filtro.sortBy().toLowerCase();
        boolean asc   = (filtro == null || filtro.asc() == null) ? true : filtro.asc();

        boolean hasTermo = StringUtils.hasText(nomeLike);

        if (hasTermo && ativo != null) {
            return unidadeDao.searchByNomeOuGestorAndAtivoLikePagedOrdered(nomeLike.trim(), ativo, sortBy, asc, offset, limit);
        }
        if (hasTermo) {
            return unidadeDao.searchByNomeOuGestorLikePagedOrdered(nomeLike.trim(), sortBy, asc, offset, limit);
        }
        if (ativo != null) {
            return unidadeDao.listByAtivoPagedOrdered(ativo, sortBy, asc, offset, limit);
        }
        return unidadeDao.listPagedOrdered(sortBy, asc, offset, limit);
    }



    @Override
    public void deactivate(Long id) {
        unidadeDao.deactivate(id);
    }

    @Override
    public void activate(Long id) {
        unidadeDao.reactivate(id);
    }
}

package com.mmx.medimetrix.application.criterio.service;

import com.mmx.medimetrix.application.criterio.commands.CriterioCreate;
import com.mmx.medimetrix.application.criterio.commands.CriterioUpdate;
import com.mmx.medimetrix.application.criterio.port.out.CriterioDao;
import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.domain.core.Criterio;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CriterioServiceImpl implements CriterioService {

    private final CriterioDao dao;

    public CriterioServiceImpl(CriterioDao dao) {
        this.dao = dao;
    }

    @Override
    public Criterio create(CriterioCreate cmd) {
        if (cmd == null || !StringUtils.hasText(cmd.nome())) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        Criterio c = new Criterio();
        c.setNome(cmd.nome().trim());
        c.setDefinicaoOperacional(cmd.definicaoOperacional());
        c.setDescricao(cmd.descricao());
        c.setAtivo(true);
        Long id = dao.insert(c);
        return dao.findById(id).orElseThrow(() -> new NoSuchElementException("Critério não encontrado após inserir."));
    }

    @Override
    public Criterio update(Long id, CriterioUpdate cmd) {
        Criterio atual = dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Critério não encontrado."));

        if (cmd != null) {
            if (StringUtils.hasText(cmd.nome())) {
                atual.setNome(cmd.nome().trim());
            }
            if (cmd.definicaoOperacional() != null) {
                atual.setDefinicaoOperacional(cmd.definicaoOperacional());
            }
            if (cmd.descricao() != null) {
                atual.setDescricao(cmd.descricao());
            }
            if (cmd.ativo() != null) {
                atual.setAtivo(cmd.ativo());
            }
        }

        dao.update(atual);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Critério não encontrado após atualizar."));
    }


    @Override
    public Optional<Criterio> findById(Long id) { return dao.findById(id); }

    @Override
    public List<Criterio> list(CriterioFiltro filtro) {
        int page = (filtro == null || filtro.page() == null || filtro.page() < 0) ? 0 : filtro.page();
        int size = (filtro == null || filtro.size() == null || filtro.size() < 1 || filtro.size() > 100) ? 20 : filtro.size();
        int offset = page * size;
        int limit = size;
        String nome = (filtro == null) ? null : filtro.nomeLike();
        if (StringUtils.hasText(nome)) {
            return dao.searchByNomeLikePaged(nome.trim(), offset, limit);
        }
        return dao.listPaged(offset, limit);
    }

    @Override
    public void activate(Long id) { dao.reactivate(id); }

    @Override
    public void deactivate(Long id) { dao.deactivate(id); }
}

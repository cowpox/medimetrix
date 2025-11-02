package com.mmx.medimetrix.application.unidade.service;

import com.mmx.medimetrix.application.unidade.commands.UnidadeCreate;
import com.mmx.medimetrix.application.unidade.port.out.UnidadeDao;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.domain.core.Unidade;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UnidadeServiceImpl implements UnidadeService {

    private final UnidadeDao unidadeDao;

    public UnidadeServiceImpl(UnidadeDao unidadeDao) {
        this.unidadeDao = unidadeDao;
    }

    @Override
    public Unidade create(UnidadeCreate cmd) {
        if (cmd == null || !StringUtils.hasText(cmd.nome())) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        Unidade nova = new Unidade();
        nova.setNome(cmd.nome().trim());
        nova.setAtivo(true);

        Long id = unidadeDao.insert(nova);
        return unidadeDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Unidade não encontrada após inserir."));
    }

    @Override
    public Unidade update(Long id, String nome, Boolean ativo) {
        Unidade atual = unidadeDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Unidade não encontrada."));

        if (StringUtils.hasText(nome)) atual.setNome(nome.trim());
        if (ativo != null)            atual.setAtivo(ativo);

        unidadeDao.update(atual);
        return unidadeDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Unidade não encontrada após atualizar."));
    }

    @Override
    public Optional<Unidade> findById(Long id) {
        return unidadeDao.findById(id);
    }

    @Override
    public List<Unidade> list(UnidadeFiltro filtro) {
        // Conversão page/size -> offset/limit
        int page = (filtro == null || filtro.page() == null || filtro.page() < 0) ? 0 : filtro.page();
        int size = (filtro == null || filtro.size() == null || filtro.size() < 1 || filtro.size() > 100) ? 20 : filtro.size();
        int offset = page * size;
        int limit  = size;

        String nomeLike = (filtro == null) ? null : filtro.nomeLike();
        if (StringUtils.hasText(nomeLike)) {
            return unidadeDao.searchByNomeLikePaged(nomeLike.trim(), offset, limit);
        }
        // Se não há filtro por nome, paginar (sem sort no DAO)
        return unidadeDao.listPaged(offset, limit);
    }

    // Conveniências (opcionalmente usadas pelos controllers):
    public List<Unidade> listAtivas() {
        return unidadeDao.listAllActive();
    }

    public List<Unidade> listByGestor(Long gestorId) {
        return unidadeDao.listByGestor(gestorId);
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

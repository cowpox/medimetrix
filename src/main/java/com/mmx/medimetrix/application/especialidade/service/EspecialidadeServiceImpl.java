package com.mmx.medimetrix.application.especialidade.service;

import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeCreate;
import com.mmx.medimetrix.application.especialidade.port.out.EspecialidadeDao;
import com.mmx.medimetrix.application.especialidade.queries.EspecialidadeFiltro;
import com.mmx.medimetrix.domain.core.Especialidade;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EspecialidadeServiceImpl implements EspecialidadeService {

    private final EspecialidadeDao dao;

    public EspecialidadeServiceImpl(EspecialidadeDao dao) {
        this.dao = dao;
    }

    @Override
    public Especialidade create(EspecialidadeCreate cmd) {
        if (cmd == null || !StringUtils.hasText(cmd.nome())) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        Especialidade nova = new Especialidade();
        nova.setNome(cmd.nome().trim());
        nova.setAtivo(true);

        Long id = dao.insert(nova);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Especialidade não encontrada após inserir."));
    }

    @Override
    public Especialidade update(Long id, String nome, Boolean ativo) {
        Especialidade atual = dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Especialidade não encontrada."));

        if (StringUtils.hasText(nome)) atual.setNome(nome.trim());
        if (ativo != null)            atual.setAtivo(ativo);

        dao.update(atual);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Especialidade não encontrada após atualizar."));
    }

    @Override
    public Optional<Especialidade> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public List<Especialidade> list(EspecialidadeFiltro filtro) {
        int page = (filtro == null || filtro.page() == null || filtro.page() < 0) ? 0 : filtro.page();
        int size = (filtro == null || filtro.size() == null || filtro.size() < 1 || filtro.size() > 100) ? 20 : filtro.size();
        int offset = page * size;
        int limit  = size;

        String nomeLike = (filtro == null) ? null : filtro.nomeLike();
        if (StringUtils.hasText(nomeLike)) {
            return dao.searchByNomeLikePaged(nomeLike.trim(), offset, limit);
        }
        return dao.listPaged(offset, limit);
    }

    // Conveniências
    public List<Especialidade> listAtivas() {
        return dao.listAllActive();
    }

    @Override
    public void deactivate(Long id) {
        dao.deactivate(id);
    }

    @Override
    public void activate(Long id) {
        dao.reactivate(id);
    }
}

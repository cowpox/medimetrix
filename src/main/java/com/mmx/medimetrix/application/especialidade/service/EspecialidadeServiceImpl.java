package com.mmx.medimetrix.application.especialidade.service;

import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeCreate;
import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeUpdate;
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
    public Especialidade update(Long id, EspecialidadeUpdate cmd) {
        Especialidade atual = dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Especialidade não encontrada."));

        if (StringUtils.hasText(cmd.nome())) atual.setNome(cmd.nome().trim());
        if (cmd.ativo() != null)            atual.setAtivo(cmd.ativo());

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
        Boolean ativo   = (filtro == null) ? null : filtro.ativo();

        // ordenação
        String sortBy = (filtro == null || filtro.sortBy() == null) ? "nome" : filtro.sortBy().toLowerCase();
        boolean asc   = (filtro == null || filtro.asc() == null) ? true : filtro.asc();

        boolean hasNome = org.springframework.util.StringUtils.hasText(nomeLike);
        if (hasNome && ativo != null) {
            return dao.searchByNomeAndAtivoLikePagedOrdered(nomeLike.trim(), ativo, sortBy, asc, offset, limit);
        }
        if (hasNome) {
            return dao.searchByNomeLikePagedOrdered(nomeLike.trim(), sortBy, asc, offset, limit);
        }
        if (ativo != null) {
            return dao.listByAtivoPagedOrdered(ativo, sortBy, asc, offset, limit);
        }
        return dao.listPagedOrdered(sortBy, asc, offset, limit);
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

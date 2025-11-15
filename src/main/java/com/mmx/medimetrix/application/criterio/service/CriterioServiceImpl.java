package com.mmx.medimetrix.application.criterio.service;

import com.mmx.medimetrix.application.criterio.commands.CriterioCreate;
import com.mmx.medimetrix.application.criterio.commands.CriterioUpdate;
import com.mmx.medimetrix.application.criterio.port.out.CriterioDao;
import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.domain.core.Criterio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

        // peso
        c.setPeso(cmd.peso() != null ? cmd.peso() : BigDecimal.ONE);

        // ordem: se não vier, entra no final
        Integer ordem = cmd.ordemSugerida();
        if (ordem == null) {
            Integer max = dao.findMaxOrdem();
            ordem = (max == null ? 0 : max) + 1;
        }
        c.setOrdemSugerida(ordem);

        // ativo
        c.setAtivo(cmd.ativo() != null ? cmd.ativo() : Boolean.TRUE);

        Long id = dao.insert(c);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Critério não encontrado após inserir."));
    }



    @Override
    public Criterio update(Long id, CriterioUpdate cmd) {
        Criterio atual = dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Critério não encontrado."));

        boolean mudouConteudo = false;

        if (cmd != null) {
            if (StringUtils.hasText(cmd.nome()) && !cmd.nome().trim().equals(atual.getNome())) {
                atual.setNome(cmd.nome().trim());
                mudouConteudo = true;
            }
            if (cmd.definicaoOperacional() != null &&
                    !cmd.definicaoOperacional().equals(atual.getDefinicaoOperacional())) {
                atual.setDefinicaoOperacional(cmd.definicaoOperacional());
                mudouConteudo = true;
            }
            if (cmd.descricao() != null &&
                    !cmd.descricao().equals(atual.getDescricao())) {
                atual.setDescricao(cmd.descricao());
                mudouConteudo = true;
            }
            if (cmd.peso() != null &&
                    (atual.getPeso() == null || cmd.peso().compareTo(atual.getPeso()) != 0)) {
                atual.setPeso(cmd.peso());
                mudouConteudo = true;
            }

            // NÃO contam para versão:
            if (cmd.ordemSugerida() != null) {
                atual.setOrdemSugerida(cmd.ordemSugerida());
            }
            if (cmd.ativo() != null) {
                atual.setAtivo(cmd.ativo());
            }
        }

        if (mudouConteudo) {
            Integer v = atual.getVersao();
            if (v == null) v = 1;
            atual.setVersao(v + 1);
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

    @Override
    @Transactional
    public void moveUp(Long id) {
        if (id == null) return;

        // antes: List<Criterio> ativos = dao.listAllActive();
        List<Criterio> criterios = dao.listAllOrdered();

        for (int i = 0; i < criterios.size(); i++) {
            Criterio atual = criterios.get(i);
            if (atual.getIdCriterio().equals(id)) {
                if (i == 0) return; // já é o primeiro da lista
                Long idAnterior = criterios.get(i - 1).getIdCriterio();
                dao.swapOrdem(id, idAnterior);
                return;
            }
        }
    }

    @Override
    @Transactional
    public void moveDown(Long id) {
        if (id == null) return;

        // antes: List<Criterio> ativos = dao.listAllActive();
        List<Criterio> criterios = dao.listAllOrdered();

        for (int i = 0; i < criterios.size(); i++) {
            Criterio atual = criterios.get(i);
            if (atual.getIdCriterio().equals(id)) {
                if (i == criterios.size() - 1) return; // já é o último
                Long idProximo = criterios.get(i + 1).getIdCriterio();
                dao.swapOrdem(id, idProximo);
                return;
            }
        }
    }

    @Override
    public int getMaxOrdem() {
        Integer max = dao.findMaxOrdem();
        return (max != null) ? max : 0;
    }



}

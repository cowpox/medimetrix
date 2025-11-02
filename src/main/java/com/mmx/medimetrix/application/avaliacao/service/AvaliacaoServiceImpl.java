package com.mmx.medimetrix.application.avaliacao.service;

import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoCreate;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoNaoEncontradaException;
import com.mmx.medimetrix.application.avaliacao.port.out.AvaliacaoDao;
import com.mmx.medimetrix.domain.core.Avaliacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoDao dao;

    public AvaliacaoServiceImpl(AvaliacaoDao dao) {
        this.dao = dao;
    }

    @Override
    public Long create(AvaliacaoCreate cmd) {
        Avaliacao a = new Avaliacao();
        a.setTitulo(cmd.getTitulo());
        a.setDataInicioAplic(cmd.getDataInicioAplic());
        a.setDataFimAplic(cmd.getDataFimAplic());
        a.setkMinimo(cmd.getkMinimo());
        a.setStatus(cmd.getStatus());
        a.setAtivo(cmd.getAtivo() != null ? cmd.getAtivo() : Boolean.TRUE);
        return dao.insert(a);
    }

    @Override
    public void update(Long id, AvaliacaoCreate cmd) {
        Avaliacao atual = dao.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        atual.setIdAvaliacao(id);
        if (cmd.getTitulo() != null) atual.setTitulo(cmd.getTitulo());
        if (cmd.getDataInicioAplic() != null) atual.setDataInicioAplic(cmd.getDataInicioAplic());
        if (cmd.getDataFimAplic() != null) atual.setDataFimAplic(cmd.getDataFimAplic());
        if (cmd.getkMinimo() != null) atual.setkMinimo(cmd.getkMinimo());
        if (cmd.getStatus() != null) atual.setStatus(cmd.getStatus());
        if (cmd.getAtivo() != null) atual.setAtivo(cmd.getAtivo());

        int rows = dao.update(atual);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Avaliacao> findById(Long id) {
        return dao.findById(id);
    }

    // ====== Listagens paginadas ======
    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> listPaged(int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        return dao.listPaged(offset, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> listByStatus(String status, int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        return dao.listByStatus(status, offset, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> searchByTituloLike(String termo, int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        return dao.searchByTituloLikePaged(termo, offset, size);
    }

    @Override
    public void ativar(Long id) {
        int rows = dao.reactivate(id);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }

    @Override
    public void desativar(Long id) {
        int rows = dao.deactivate(id);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }

    @Override
    public void delete(Long id) {
        int rows = dao.deleteById(id);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }
}

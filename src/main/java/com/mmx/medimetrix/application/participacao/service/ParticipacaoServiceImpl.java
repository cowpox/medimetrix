package com.mmx.medimetrix.application.participacao.service;

import com.mmx.medimetrix.application.participacao.commands.ParticipacaoCreate;
import com.mmx.medimetrix.application.participacao.commands.ParticipacaoUpdate;
import com.mmx.medimetrix.application.participacao.exceptions.ParticipacaoNaoEncontradaException;
import com.mmx.medimetrix.application.participacao.port.out.ParticipacaoDao;
import com.mmx.medimetrix.domain.core.Participacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParticipacaoServiceImpl implements ParticipacaoService {

    private final ParticipacaoDao dao;

    public ParticipacaoServiceImpl(ParticipacaoDao dao) {
        this.dao = dao;
    }

    @Override
    public Long create(ParticipacaoCreate cmd) {
        Participacao p = new Participacao();
        p.setIdAvaliacao(cmd.getIdAvaliacao());
        p.setAvaliadoMedicoId(cmd.getAvaliadoMedicoId());
        p.setUnidadeSnapshotId(cmd.getUnidadeSnapshotId());
        p.setEspecialidadeSnapshotId(cmd.getEspecialidadeSnapshotId());
        p.setStatus(cmd.getStatus());
        return dao.insert(p);
    }

    @Override
    public void update(Long id, ParticipacaoUpdate cmd) {
        Participacao atual = dao.findById(id).orElseThrow(ParticipacaoNaoEncontradaException::new);

        if (cmd.getAvaliadoMedicoId() != null) atual.setAvaliadoMedicoId(cmd.getAvaliadoMedicoId());
        if (cmd.getUnidadeSnapshotId() != null) atual.setUnidadeSnapshotId(cmd.getUnidadeSnapshotId());
        if (cmd.getEspecialidadeSnapshotId() != null) atual.setEspecialidadeSnapshotId(cmd.getEspecialidadeSnapshotId());
        if (cmd.getStatus() != null) atual.setStatus(cmd.getStatus());

        int rows = dao.update(atual);
        if (rows == 0) throw new ParticipacaoNaoEncontradaException();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Participacao> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Participacao> findByAvaliacaoAndMedico(Long idAvaliacao, Long medicoId) {
        return dao.findByAvaliacaoAndMedico(idAvaliacao, medicoId);
    }

    private int offset(int page, int size) {
        return Math.max(page, 0) * Math.max(size, 1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Participacao> listByAvaliacao(Long idAvaliacao, int page, int size) {
        return dao.listByAvaliacao(idAvaliacao, offset(page, size), size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Participacao> listByMedico(Long medicoId, int page, int size) {
        return dao.listByMedico(medicoId, offset(page, size), size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Participacao> listByStatus(String status, int page, int size) {
        return dao.listByStatus(status, offset(page, size), size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Participacao> listPaged(int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        return dao.listPaged(offset, size);
    }


    @Override
    public void markStarted(Long idParticipacao) {
        if (dao.markStarted(idParticipacao) == 0) throw new ParticipacaoNaoEncontradaException();
    }

    @Override
    public void touchActivity(Long idParticipacao) {
        if (dao.touchActivity(idParticipacao) == 0) throw new ParticipacaoNaoEncontradaException();
    }

    @Override
    public void markSubmitted(Long idParticipacao) {
        if (dao.markSubmitted(idParticipacao) == 0) throw new ParticipacaoNaoEncontradaException();
    }

    @Override
    public void delete(Long idParticipacao) {
        if (dao.deleteById(idParticipacao) == 0) throw new ParticipacaoNaoEncontradaException();
    }
}

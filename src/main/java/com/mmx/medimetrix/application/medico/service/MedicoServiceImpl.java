package com.mmx.medimetrix.application.medico.service;

import com.mmx.medimetrix.application.medico.commands.MedicoCreate;
import com.mmx.medimetrix.application.medico.commands.MedicoUpdate;
import com.mmx.medimetrix.application.medico.exceptions.MedicoNaoEncontradoException;
import com.mmx.medimetrix.application.medico.port.out.MedicoDao;
import com.mmx.medimetrix.domain.core.Medico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicoServiceImpl implements MedicoService {

    private final MedicoDao dao;

    public MedicoServiceImpl(MedicoDao dao) { this.dao = dao; }

    private int offset(int page, int size) {
        return Math.max(page, 0) * Math.max(size, 1);
    }

    @Override
    public Long create(MedicoCreate cmd) {
        Medico m = new Medico();
        m.setUsuarioId(cmd.getUsuarioId());
        m.setEspecialidadeId(cmd.getEspecialidadeId());
        m.setUnidadeId(cmd.getUnidadeId());
        m.setCrmNumero(cmd.getCrmNumero());
        m.setCrmUf(cmd.getCrmUf());
        return dao.insert(m);
    }

    @Override
    public void update(Long usuarioId, MedicoUpdate cmd) {
        Medico atual = dao.findByUsuarioId(usuarioId)
                .orElseThrow(MedicoNaoEncontradoException::new);

        if (cmd.getEspecialidadeId() != null) atual.setEspecialidadeId(cmd.getEspecialidadeId());
        if (cmd.getUnidadeId() != null)       atual.setUnidadeId(cmd.getUnidadeId());
        if (cmd.getCrmNumero() != null)       atual.setCrmNumero(cmd.getCrmNumero());
        if (cmd.getCrmUf() != null)           atual.setCrmUf(cmd.getCrmUf());

        if (dao.update(atual) == 0) throw new MedicoNaoEncontradoException();
    }

    @Override @Transactional(readOnly = true)
    public Optional<Medico> findByUsuarioId(Long usuarioId) {
        return dao.findByUsuarioId(usuarioId);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Medico> findByCrm(String crmNumero, String crmUf) {
        return dao.findByCrm(crmNumero, crmUf);
    }

    @Override @Transactional(readOnly = true)
    public List<Medico> listByEspecialidade(Long especialidadeId, int page, int size) {
        return dao.listByEspecialidade(especialidadeId, offset(page, size), size);
    }

    @Override @Transactional(readOnly = true)
    public List<Medico> listByUnidade(Long unidadeId, int page, int size) {
        return dao.listByUnidade(unidadeId, offset(page, size), size);
    }

    @Override @Transactional(readOnly = true)
    public List<Medico> listPaged(int page, int size) {
        return dao.listPaged(offset(page, size), size);
    }

    @Override
    public void deleteByUsuarioId(Long usuarioId) {
        if (dao.deleteByUsuarioId(usuarioId) == 0) throw new MedicoNaoEncontradoException();
    }
}

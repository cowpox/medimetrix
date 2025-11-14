package com.mmx.medimetrix.application.medico.service;

import com.mmx.medimetrix.application.medico.commands.MedicoCreate;
import com.mmx.medimetrix.application.medico.commands.MedicoUpdate;
import com.mmx.medimetrix.application.medico.exceptions.MedicoNaoEncontradoException;
import com.mmx.medimetrix.application.medico.exceptions.CrmDuplicadoException;
import com.mmx.medimetrix.application.medico.port.out.MedicoDao;
import com.mmx.medimetrix.application.participacao.port.out.ParticipacaoDao;
import com.mmx.medimetrix.domain.core.Medico;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicoServiceImpl implements MedicoService {

    private final MedicoDao dao;
    private final ParticipacaoDao participacaoDao;   // NOVO

    public MedicoServiceImpl(MedicoDao dao, ParticipacaoDao participacaoDao) {
        this.dao = dao;
        this.participacaoDao = participacaoDao;
    }

    private int offset(int page, int size) {
        return Math.max(page, 0) * Math.max(size, 1);
    }

    @Override
    public Long create(MedicoCreate cmd) {
        // normalizações simples (opcional, mas ajuda a consistência)
        String numero = cmd.getCrmNumero() == null ? null : cmd.getCrmNumero().trim();
        String uf     = cmd.getCrmUf() == null ? null : cmd.getCrmUf().trim().toUpperCase();

        // 1) validação explícita de duplicidade
        dao.findByCrm(numero, uf)
                .ifPresent(m -> { throw new CrmDuplicadoException(numero, uf); });

        try {
            Medico m = new Medico();
            m.setUsuarioId(cmd.getUsuarioId());
            m.setEspecialidadeId(cmd.getEspecialidadeId());
            m.setUnidadeId(cmd.getUnidadeId());
            m.setCrmNumero(numero);
            m.setCrmUf(uf);
            return dao.insert(m);

        } catch (DataIntegrityViolationException e) {
            // 2) fallback: se bater UNIQUE no banco por corrida, traduz para exceção de domínio
            Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
            String sqlState = null;
            try {
                // disponível quando Postgres
                sqlState = (String) root.getClass().getMethod("getSQLState").invoke(root);
            } catch (Exception ignore) {}

            if ("23505".equals(sqlState)) { // unique_violation no Postgres
                throw new CrmDuplicadoException(numero, uf);
            }
            throw e; // outras violações seguem o fluxo padrão
        }
    }

    @Override
    public void update(Long usuarioId, MedicoUpdate cmd) {
        Medico atual = dao.findByUsuarioId(usuarioId)
                .orElseThrow(MedicoNaoEncontradoException::new);

        // Aplica alterações + normalização de CRM
        if (cmd.getEspecialidadeId() != null) {
            atual.setEspecialidadeId(cmd.getEspecialidadeId());
        }
        if (cmd.getUnidadeId() != null) {
            atual.setUnidadeId(cmd.getUnidadeId());
        }
        if (cmd.getCrmNumero() != null) {
            atual.setCrmNumero(cmd.getCrmNumero().trim());
        }
        if (cmd.getCrmUf() != null) {
            atual.setCrmUf(cmd.getCrmUf().trim().toUpperCase());
        }

        String numero = atual.getCrmNumero();
        String uf     = atual.getCrmUf();

        // 1) Validação explícita de duplicidade:
        //    se existir OUTRO médico com o mesmo CRM, lança exceção.
        dao.findByCrm(numero, uf).ifPresent(m -> {
            if (!m.getUsuarioId().equals(usuarioId)) {
                throw new CrmDuplicadoException(numero, uf);
            }
        });

        try {
            int rows = dao.update(atual);
            if (rows == 0) {
                throw new MedicoNaoEncontradoException();
            }

        } catch (DataIntegrityViolationException e) {
            Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
            String sqlState = null;
            try {
                sqlState = (String) root.getClass().getMethod("getSQLState").invoke(root);
            } catch (Exception ignore) {}

            if ("23505".equals(sqlState)) { // unique_violation no Postgres
                throw new CrmDuplicadoException(numero, uf);
            }
            throw e;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Medico> findByUsuarioId(Long usuarioId) {
        return dao.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medico> findByCrm(String crmNumero, String crmUf) {
        return dao.findByCrm(crmNumero, crmUf);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> listByEspecialidade(Long especialidadeId, int page, int size) {
        return dao.listByEspecialidade(especialidadeId, offset(page, size), size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> listByUnidade(Long unidadeId, int page, int size) {
        return dao.listByUnidade(unidadeId, offset(page, size), size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> listPaged(int page, int size) {
        return dao.listPaged(offset(page, size), size);
    }

    @Override
    public void deleteByUsuarioId(Long usuarioId) {
        if (dao.deleteByUsuarioId(usuarioId) == 0) throw new MedicoNaoEncontradoException();
    }

    @Override
    public boolean deleteIfSemVinculos(Long usuarioId) {
        // Verifica se existe ao menos 1 participação para este médico.
        // Se houver, não exclui e retorna false.
        boolean hasParticipacoes =
                !participacaoDao.listByMedico(usuarioId, 0, 1).isEmpty();

        if (hasParticipacoes) {
            return false;
        }

        // Sem vínculos: exclui apenas o registro em MEDICO (usuário permanece).
        if (dao.deleteByUsuarioId(usuarioId) == 0) {
            throw new MedicoNaoEncontradoException();
        }

        return true;
    }
}

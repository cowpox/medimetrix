package com.mmx.medimetrix.application.resposta.service;

import com.mmx.medimetrix.application.resposta.commands.RespostaCreate;
import com.mmx.medimetrix.application.resposta.commands.RespostaUpdateValores;
import com.mmx.medimetrix.application.resposta.exceptions.RespostaNaoEncontradaException;
import com.mmx.medimetrix.application.resposta.port.out.RespostaDao;
import com.mmx.medimetrix.domain.core.Resposta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RespostaServiceImpl implements RespostaService {

    private final RespostaDao dao;

    public RespostaServiceImpl(RespostaDao dao) {
        this.dao = dao;
    }

    @Override
    public Long create(RespostaCreate cmd) {
        Resposta r = new Resposta();
        r.setIdParticipacao(cmd.getIdParticipacao());
        r.setIdAvaliacao(cmd.getIdAvaliacao());
        r.setIdQuestao(cmd.getIdQuestao());
        r.setValorNumerico(cmd.getValorNumerico());
        r.setValorBinario(cmd.getValorBinario());
        r.setTexto(cmd.getTexto());
        return dao.insert(r);
    }

    @Override
    public void updateValores(Long idResposta, RespostaUpdateValores cmd) {
        Resposta atual = dao.findById(idResposta)
                .orElseThrow(RespostaNaoEncontradaException::new);

        if (cmd.getValorNumerico() != null) atual.setValorNumerico(cmd.getValorNumerico());
        if (cmd.getValorBinario() != null)  atual.setValorBinario(cmd.getValorBinario());
        if (cmd.getTexto() != null)         atual.setTexto(cmd.getTexto());

        if (dao.updateValores(atual) == 0) throw new RespostaNaoEncontradaException();
    }

    @Override @Transactional(readOnly = true)
    public Optional<Resposta> findById(Long idResposta) {
        return dao.findById(idResposta);
    }

    private int offset(int page, int size) {
        return Math.max(page, 0) * Math.max(size, 1);
    }

    @Override @Transactional(readOnly = true)
    public List<Resposta> listByParticipacao(Long idParticipacao) {
        return dao.listByParticipacao(idParticipacao);
    }

    @Override @Transactional(readOnly = true)
    public List<Resposta> listByAvaliacao(Long idAvaliacao, int page, int size) {
        return dao.listByAvaliacao(idAvaliacao, offset(page, size), size);
    }

    @Override @Transactional(readOnly = true)
    public List<Resposta> listByQuestao(Long idQuestao, int page, int size) {
        return dao.listByQuestao(idQuestao, offset(page, size), size);
    }

    @Override @Transactional(readOnly = true)
    public List<Resposta> listPaged(int page, int size) {
        int off = Math.max(page, 0) * Math.max(size, 1);
        return dao.listPaged(off, size);
    }

    @Override
    public void deleteById(Long idResposta) {
        if (dao.deleteById(idResposta) == 0) throw new RespostaNaoEncontradaException();
    }

    @Override
    public void deleteByParticipacao(Long idParticipacao) {
        dao.deleteByParticipacao(idParticipacao);
    }
}

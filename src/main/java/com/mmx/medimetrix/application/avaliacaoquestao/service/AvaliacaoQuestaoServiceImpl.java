package com.mmx.medimetrix.application.avaliacaoquestao.service;

import com.mmx.medimetrix.application.avaliacaoquestao.commands.AvaliacaoQuestaoCreate;
import com.mmx.medimetrix.application.avaliacaoquestao.commands.AvaliacaoQuestaoUpdate;
import com.mmx.medimetrix.application.avaliacaoquestao.exceptions.AvaliacaoQuestaoNaoEncontradaException;
import com.mmx.medimetrix.application.avaliacaoquestao.port.out.AvaliacaoQuestaoDao;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvaliacaoQuestaoServiceImpl implements AvaliacaoQuestaoService {

    private final AvaliacaoQuestaoDao dao;

    public AvaliacaoQuestaoServiceImpl(AvaliacaoQuestaoDao dao) {
        this.dao = dao;
    }

    @Override
    public void addQuestao(Long idAvaliacao, AvaliacaoQuestaoCreate cmd) {
        AvaliacaoQuestao aq = new AvaliacaoQuestao();
        aq.setIdAvaliacao(idAvaliacao);
        aq.setIdQuestao(cmd.getIdQuestao());
        aq.setOrdem(cmd.getOrdem());
        aq.setObrigatoriaNaAval(cmd.getObrigatoriaNaAval());
        aq.setAtivaNaAval(cmd.getAtivaNaAval());
        aq.setPesoNaAval(cmd.getPesoNaAval());
        aq.setVisivelParaGestorLocal(cmd.getVisivelParaGestorLocal());
        aq.setObservacaoAdmin(cmd.getObservacaoAdmin());

        int rows = dao.insert(aq);
        if (rows == 0) {
            throw new AvaliacaoQuestaoNaoEncontradaException("Falha ao criar vínculo Avaliação↔Questão");
        }
    }

    @Override
    public void update(Long idAvaliacao, Long idQuestao, AvaliacaoQuestaoUpdate cmd) {
        AvaliacaoQuestao atual = dao.findOne(idAvaliacao, idQuestao)
                .orElseThrow(AvaliacaoQuestaoNaoEncontradaException::new);

        if (cmd.getOrdem() != null) atual.setOrdem(cmd.getOrdem());
        if (cmd.getObrigatoriaNaAval() != null) atual.setObrigatoriaNaAval(cmd.getObrigatoriaNaAval());
        if (cmd.getAtivaNaAval() != null) atual.setAtivaNaAval(cmd.getAtivaNaAval());
        if (cmd.getPesoNaAval() != null) atual.setPesoNaAval(cmd.getPesoNaAval());
        if (cmd.getVisivelParaGestorLocal() != null) atual.setVisivelParaGestorLocal(cmd.getVisivelParaGestorLocal());
        if (cmd.getObservacaoAdmin() != null) atual.setObservacaoAdmin(cmd.getObservacaoAdmin());

        int rows = dao.update(atual);
        if (rows == 0) throw new AvaliacaoQuestaoNaoEncontradaException();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AvaliacaoQuestao> findOne(Long idAvaliacao, Long idQuestao) {
        return dao.findOne(idAvaliacao, idQuestao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvaliacaoQuestao> listByAvaliacao(Long idAvaliacao) {
        return dao.listByAvaliacao(idAvaliacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvaliacaoQuestao> listByQuestao(Long idQuestao) {
        return dao.listByQuestao(idQuestao);
    }

    @Override
    public void deleteOne(Long idAvaliacao, Long idQuestao) {
        int rows = dao.deleteOne(idAvaliacao, idQuestao);
        if (rows == 0) throw new AvaliacaoQuestaoNaoEncontradaException();
    }

    @Override
    public void deleteAllByAvaliacao(Long idAvaliacao) {
        dao.deleteAllByAvaliacao(idAvaliacao);
    }

    @Override
    public void swapOrdem(Long idAvaliacao, Long idQuestaoA, Long idQuestaoB) {
        int rows = dao.swapOrdem(idAvaliacao, idQuestaoA, idQuestaoB);
        if (rows < 3) {
            throw new AvaliacaoQuestaoNaoEncontradaException("Não foi possível reordenar as questões");
        }
    }

    @Override
    public void addQuestaoAutoOrdem(Long idAvaliacao, Long idQuestao) {
        // pega a maior ordem atual e adiciona +1
        List<AvaliacaoQuestao> existentes = dao.listByAvaliacao(idAvaliacao);
        int proximaOrdem = existentes.stream()
                .map(AvaliacaoQuestao::getOrdem)
                .filter(o -> o != null && o > 0)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        AvaliacaoQuestaoCreate cmd = new AvaliacaoQuestaoCreate();
        cmd.setIdQuestao(idQuestao);
        cmd.setOrdem(proximaOrdem);
        cmd.setAtivaNaAval(Boolean.TRUE);

        addQuestao(idAvaliacao, cmd);
    }

    @Override
    public void renumerarOrdem(Long idAvaliacao) {
        List<AvaliacaoQuestao> lista = dao.listByAvaliacao(idAvaliacao);

        // idealmente o DAO já devolve ordenado por ordem; se não, ordena aqui:
        lista.sort((a, b) -> Integer.compare(
                a.getOrdem() != null ? a.getOrdem() : Integer.MAX_VALUE,
                b.getOrdem() != null ? b.getOrdem() : Integer.MAX_VALUE
        ));

        int ordem = 1;
        for (AvaliacaoQuestao aq : lista) {
            aq.setOrdem(ordem++);
            dao.update(aq);
        }
    }

}

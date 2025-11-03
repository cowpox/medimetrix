package com.mmx.medimetrix.application.questao.service;

import com.mmx.medimetrix.application.questao.commands.QuestaoCreate;
import com.mmx.medimetrix.application.questao.commands.QuestaoUpdate;
import com.mmx.medimetrix.application.questao.port.out.QuestaoDao;
import com.mmx.medimetrix.application.questao.queries.QuestaoFiltro;
import com.mmx.medimetrix.domain.core.Questao;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class QuestaoServiceImpl implements QuestaoService {

    private final QuestaoDao dao;

    public QuestaoServiceImpl(QuestaoDao dao) { this.dao = dao; }

    @Override
    public Questao create(QuestaoCreate cmd) {
        if (cmd == null || cmd.idCriterio() == null || !StringUtils.hasText(cmd.enunciado())) {
            throw new IllegalArgumentException("Critério e enunciado são obrigatórios.");
        }
        var q = new Questao();
        q.setIdCriterio(cmd.idCriterio());
        q.setEnunciado(cmd.enunciado().trim());
        q.setTipo(cmd.tipo());
        q.setObrigatoriedade(cmd.obrigatoriedade());
        q.setValidacaoNumMin(cmd.validacaoNumMin());            // BigDecimal
        q.setValidacaoNumMax(cmd.validacaoNumMax());            // BigDecimal
        q.setTamanhoTextoMax(cmd.tamanhoTextoMax());
        q.setSensivel(cmd.sensivel());
        q.setVisivelParaGestor(cmd.visivelParaGestor());
        q.setOrdemSugerida(cmd.ordemSugerida());
        q.setAtivo(true);

        Long id = dao.insert(q);
        return dao.findById(id).orElseThrow(() -> new NoSuchElementException("Questão não encontrada após inserir."));
    }


    @Override
    public Questao update(Long id, QuestaoUpdate cmd) {
        var atual = dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Questão não encontrada."));

        if (cmd != null) {
            if (StringUtils.hasText(cmd.enunciado())) atual.setEnunciado(cmd.enunciado().trim());
            if (cmd.tipo() != null) atual.setTipo(cmd.tipo());
            if (cmd.obrigatoriedade() != null) atual.setObrigatoriedade(cmd.obrigatoriedade());
            if (cmd.validacaoNumMin() != null) atual.setValidacaoNumMin(cmd.validacaoNumMin());
            if (cmd.validacaoNumMax() != null) atual.setValidacaoNumMax(cmd.validacaoNumMax());
            if (cmd.tamanhoTextoMax() != null) atual.setTamanhoTextoMax(cmd.tamanhoTextoMax());
            if (cmd.sensivel() != null) atual.setSensivel(cmd.sensivel());
            if (cmd.visivelParaGestor() != null) atual.setVisivelParaGestor(cmd.visivelParaGestor());
            if (cmd.ordemSugerida() != null) atual.setOrdemSugerida(cmd.ordemSugerida());
            if (cmd.ativo() != null) atual.setAtivo(cmd.ativo());
        }

        dao.update(atual);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Questão não encontrada após atualizar."));
    }


    @Override
    public Optional<Questao> findById(Long id) { return dao.findById(id); }

    @Override
    public List<Questao> list(QuestaoFiltro filtro) {
        int page = (filtro == null || filtro.page() == null || filtro.page() < 0) ? 0 : filtro.page();
        int size = (filtro == null || filtro.size() == null || filtro.size() < 1 || filtro.size() > 100) ? 20 : filtro.size();
        int offset = page * size;
        int limit  = size;
        if (filtro != null) {
            if (filtro.idCriterio() != null) {
                return dao.listByCriterio(filtro.idCriterio(), offset, limit);
            }
            if (StringUtils.hasText(filtro.tipo())) {
                return dao.listAtivasByTipo(filtro.tipo(), offset, limit);
            }
            if (StringUtils.hasText(filtro.enunciadoLike())) {
                return dao.searchByEnunciadoLikePaged(filtro.enunciadoLike().trim(), offset, limit);
            }
        }
        return dao.listPaged(offset, limit);
    }

    @Override public void activate(Long id) { dao.reactivate(id); }
    @Override public void deactivate(Long id) { dao.deactivate(id); }
}

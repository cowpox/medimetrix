package com.mmx.medimetrix.application.questao.service;

import com.mmx.medimetrix.application.questao.commands.QuestaoCreate;
import com.mmx.medimetrix.application.questao.queries.QuestaoFiltro;
import com.mmx.medimetrix.domain.core.Questao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface QuestaoService {
    Questao create(QuestaoCreate cmd);
    Questao update(Long id, String enunciado, String tipo, String obrigatoriedade,
                   BigDecimal validacaoNumMin, BigDecimal validacaoNumMax,
                   Integer tamanhoTextoMax, Boolean sensivel, Boolean visivelParaGestor,
                   Boolean ativo, Integer ordemSugerida);

    Optional<Questao> findById(Long id);
    List<Questao> list(QuestaoFiltro filtro);
    void activate(Long id);
    void deactivate(Long id);
}

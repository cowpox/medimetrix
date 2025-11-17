package com.mmx.medimetrix.application.avaliacaoquestao.service;

import com.mmx.medimetrix.application.avaliacaoquestao.commands.AvaliacaoQuestaoCreate;
import com.mmx.medimetrix.application.avaliacaoquestao.commands.AvaliacaoQuestaoUpdate;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoQuestaoService {
    void addQuestao(Long idAvaliacao, AvaliacaoQuestaoCreate cmd);
    void update(Long idAvaliacao, Long idQuestao, AvaliacaoQuestaoUpdate cmd);
    Optional<AvaliacaoQuestao> findOne(Long idAvaliacao, Long idQuestao);
    List<AvaliacaoQuestao> listByAvaliacao(Long idAvaliacao);
    List<AvaliacaoQuestao> listByQuestao(Long idQuestao);
    void deleteOne(Long idAvaliacao, Long idQuestao);
    void deleteAllByAvaliacao(Long idAvaliacao);
    void swapOrdem(Long idAvaliacao, Long idQuestaoA, Long idQuestaoB);
    // === helpers adicionais ===
    void addQuestaoAutoOrdem(Long idAvaliacao, Long idQuestao);
    void renumerarOrdem(Long idAvaliacao);
}

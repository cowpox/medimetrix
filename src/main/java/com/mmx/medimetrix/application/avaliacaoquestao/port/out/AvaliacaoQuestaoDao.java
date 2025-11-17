package com.mmx.medimetrix.application.avaliacaoquestao.port.out;

import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import java.util.List;
import java.util.Optional;

public interface AvaliacaoQuestaoDao {
    int insert(AvaliacaoQuestao aq); // PK composta -> retorna rows affected
    int update(AvaliacaoQuestao aq); // atualiza por (idAvaliacao, idQuestao)
    Optional<AvaliacaoQuestao> findOne(Long idAvaliacao, Long idQuestao);
    List<AvaliacaoQuestao> listByAvaliacao(Long idAvaliacao);
    List<AvaliacaoQuestao> listByQuestao(Long idQuestao);
    int deleteOne(Long idAvaliacao, Long idQuestao);
    int deleteAllByAvaliacao(Long idAvaliacao);
    int swapOrdem(Long idAvaliacao, Long idQuestaoA, Long idQuestaoB); // utilitário para reordenar
    int countByAvaliacao(Long idAvaliacao);
}

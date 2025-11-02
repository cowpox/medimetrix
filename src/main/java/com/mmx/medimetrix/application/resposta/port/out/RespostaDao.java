package com.mmx.medimetrix.application.resposta.port.out;

import com.mmx.medimetrix.domain.core.Resposta;
import java.util.List;
import java.util.Optional;

public interface RespostaDao {
    Long insert(Resposta r);
    int updateValores(Resposta r); // atualiza valores por ID_RESPOSTA
    Optional<Resposta> findById(Long idResposta);
    List<Resposta> listByParticipacao(Long idParticipacao);
    List<Resposta> listByAvaliacao(Long idAvaliacao, Integer offset, Integer limit);
    List<Resposta> listByQuestao(Long idQuestao, Integer offset, Integer limit);
    int deleteById(Long idResposta);
    int deleteByParticipacao(Long idParticipacao);
}

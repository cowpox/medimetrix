package com.mmx.medimetrix.application.resposta.service;

import com.mmx.medimetrix.application.resposta.commands.RespostaCreate;
import com.mmx.medimetrix.application.resposta.commands.RespostaUpdateValores;
import com.mmx.medimetrix.domain.core.Resposta;

import java.util.List;
import java.util.Optional;

public interface RespostaService {
    Long create(RespostaCreate cmd);
    void updateValores(Long idResposta, RespostaUpdateValores cmd);
    Optional<Resposta> findById(Long idResposta);

    List<Resposta> listByParticipacao(Long idParticipacao);
    List<Resposta> listByAvaliacao(Long idAvaliacao, int page, int size);
    List<Resposta> listByQuestao(Long idQuestao, int page, int size);
    List<Resposta> listPaged(int page, int size);

    void deleteById(Long idResposta);
    void deleteByParticipacao(Long idParticipacao);
}

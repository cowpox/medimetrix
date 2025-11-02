package com.mmx.medimetrix.application.participacao.port.out;

import com.mmx.medimetrix.domain.core.Participacao;
import java.util.List;
import java.util.Optional;

public interface ParticipacaoDao {
    Long insert(Participacao p);
    int update(Participacao p);
    Optional<Participacao> findById(Long id);
    Optional<Participacao> findByAvaliacaoAndMedico(Long idAvaliacao, Long medicoId);
    List<Participacao> listByAvaliacao(Long idAvaliacao, Integer offset, Integer limit);
    List<Participacao> listByMedico(Long medicoId, Integer offset, Integer limit);
    List<Participacao> listByStatus(String status, Integer offset, Integer limit);
    List<Participacao> listPaged(Integer offset, Integer limit);
    int markStarted(Long idParticipacao);       // STATUS = EM_ANDAMENTO, STARTED_AT = now
    int touchActivity(Long idParticipacao);     // LAST_ACTIVITY_AT = now
    int markSubmitted(Long idParticipacao);     // STATUS = RESPONDIDA, SUBMITTED_AT = now
    int deleteById(Long idParticipacao);
}

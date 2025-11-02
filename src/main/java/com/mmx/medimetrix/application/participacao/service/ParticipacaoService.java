package com.mmx.medimetrix.application.participacao.service;

import com.mmx.medimetrix.application.participacao.commands.ParticipacaoCreate;
import com.mmx.medimetrix.application.participacao.commands.ParticipacaoUpdate;
import com.mmx.medimetrix.domain.core.Participacao;

import java.util.List;
import java.util.Optional;

public interface ParticipacaoService {
    Long create(ParticipacaoCreate cmd);
    void update(Long id, ParticipacaoUpdate cmd);
    Optional<Participacao> findById(Long id);

    Optional<Participacao> findByAvaliacaoAndMedico(Long idAvaliacao, Long medicoId);

    List<Participacao> listByAvaliacao(Long idAvaliacao, int page, int size);
    List<Participacao> listByMedico(Long medicoId, int page, int size);
    List<Participacao> listByStatus(String status, int page, int size);
    List<Participacao> listPaged(int page, int size);

    void markStarted(Long idParticipacao);
    void touchActivity(Long idParticipacao);
    void markSubmitted(Long idParticipacao);

    void delete(Long idParticipacao);
}

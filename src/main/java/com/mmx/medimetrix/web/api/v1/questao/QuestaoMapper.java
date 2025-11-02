package com.mmx.medimetrix.web.api.v1.questao;

import com.mmx.medimetrix.domain.core.Questao;
import com.mmx.medimetrix.web.api.v1.questao.dto.QuestaoResponseDTO;

final class QuestaoMapper {
    private QuestaoMapper() {}

    static QuestaoResponseDTO toResponse(Questao q) {
        return new QuestaoResponseDTO(
                q.getIdQuestao(),
                q.getIdCriterio(),
                q.getEnunciado(),
                q.getTipo(),
                q.getObrigatoriedade(),
                q.getValidacaoNumMin(),
                q.getValidacaoNumMax(),
                q.getTamanhoTextoMax(),
                q.getSensivel(),
                q.getVisivelParaGestor(),
                q.getVersao(),
                q.getOrdemSugerida(),
                q.getAtivo(),
                q.getDataCriacao(),
                q.getDataUltimaEdicao()
        );
    }
}

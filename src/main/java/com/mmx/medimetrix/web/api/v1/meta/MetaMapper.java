package com.mmx.medimetrix.web.api.v1.meta;

import com.mmx.medimetrix.domain.core.Meta;
import com.mmx.medimetrix.web.api.v1.meta.dto.MetaResponseDTO;

final class MetaMapper {
    private MetaMapper() {}

    static MetaResponseDTO toResponse(Meta m) {
        return new MetaResponseDTO(
                m.getIdMeta(),
                m.getIdCriterio(),
                m.getIdUnidade(),
                m.getIdEspecialidade(),
                m.getAlvo(),
                m.getOperador(),
                m.getVigenciaInicio(),
                m.getVigenciaFim(),
                m.getAtivo(),
                m.getPrioridade(),
                m.getJustificativa(),
                m.getDataCriacao(),
                m.getDataUltimaEdicao()
        );
    }
}

package com.mmx.medimetrix.web.api.v1.criterio;

import com.mmx.medimetrix.domain.core.Criterio;
import com.mmx.medimetrix.web.api.v1.criterio.dto.CriterioResponseDTO;

final class CriterioMapper {
    private CriterioMapper() {}

    static CriterioResponseDTO toResponse(Criterio c) {
        return new CriterioResponseDTO(
                c.getIdCriterio(),
                c.getNome(),
                c.getDefinicaoOperacional(),
                c.getDescricao(),
                c.getAtivo(),
                c.getPeso(),
                c.getOrdemSugerida(),
                c.getVersao(),
                c.getDataCriacao(),
                c.getDataUltimaEdicao()
        );
    }
}

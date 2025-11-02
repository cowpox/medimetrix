package com.mmx.medimetrix.web.api.v1.unidade;

import com.mmx.medimetrix.domain.core.Unidade;
import com.mmx.medimetrix.web.api.v1.unidade.dto.UnidadeResponseDTO;

final class UnidadeMapper {
    private UnidadeMapper() {}

    static UnidadeResponseDTO toResponse(Unidade u) {
        return new UnidadeResponseDTO(
                u.getIdUnidade(),
                u.getNome(),
                u.getAtivo(),
                u.getDataCriacao(),
                u.getDataUltimaEdicao()
        );
    }
}

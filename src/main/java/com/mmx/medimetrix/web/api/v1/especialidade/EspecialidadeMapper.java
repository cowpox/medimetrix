package com.mmx.medimetrix.web.api.v1.especialidade;

import com.mmx.medimetrix.domain.core.Especialidade;
import com.mmx.medimetrix.web.api.v1.especialidade.dto.EspecialidadeResponseDTO;

final class EspecialidadeMapper {
    private EspecialidadeMapper() {}

    static EspecialidadeResponseDTO toResponse(Especialidade e) {
        return new EspecialidadeResponseDTO(
                e.getIdEspecialidade(),
                e.getNome(),
                e.getAtivo(),
                e.getDataCriacao(),
                e.getDataUltimaEdicao()
        );
    }
}

package com.mmx.medimetrix.web.api.v1.medico;

import com.mmx.medimetrix.application.medico.commands.MedicoCreate;
import com.mmx.medimetrix.application.medico.commands.MedicoUpdate;
import com.mmx.medimetrix.domain.core.Medico;
import com.mmx.medimetrix.web.api.v1.medico.dto.MedicoCreateDTO;
import com.mmx.medimetrix.web.api.v1.medico.dto.MedicoResponseDTO;
import com.mmx.medimetrix.web.api.v1.medico.dto.MedicoUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper {

    public MedicoCreate toCommand(MedicoCreateDTO dto) {
        MedicoCreate cmd = new MedicoCreate();
        cmd.setUsuarioId(dto.getUsuarioId());
        cmd.setEspecialidadeId(dto.getEspecialidadeId());
        cmd.setUnidadeId(dto.getUnidadeId());
        cmd.setCrmNumero(dto.getCrmNumero());
        cmd.setCrmUf(dto.getCrmUf());
        return cmd;
    }

    public MedicoUpdate toCommand(MedicoUpdateDTO dto, Medico atual) {
        MedicoUpdate cmd = new MedicoUpdate();
        cmd.setEspecialidadeId(dto.getEspecialidadeId() != null ? dto.getEspecialidadeId() : atual.getEspecialidadeId());
        cmd.setUnidadeId(dto.getUnidadeId() != null ? dto.getUnidadeId() : atual.getUnidadeId());
        cmd.setCrmNumero(dto.getCrmNumero() != null ? dto.getCrmNumero() : atual.getCrmNumero());
        cmd.setCrmUf(dto.getCrmUf() != null ? dto.getCrmUf() : atual.getCrmUf());
        return cmd;
    }

    public MedicoResponseDTO toDTO(Medico m) {
        MedicoResponseDTO dto = new MedicoResponseDTO();
        dto.setUsuarioId(m.getUsuarioId());
        dto.setEspecialidadeId(m.getEspecialidadeId());
        dto.setUnidadeId(m.getUnidadeId());
        dto.setCrmNumero(m.getCrmNumero());
        dto.setCrmUf(m.getCrmUf());
        dto.setDataCriacao(m.getDataCriacao());
        dto.setDataUltimaEdicao(m.getDataUltimaEdicao());
        return dto;
    }
}

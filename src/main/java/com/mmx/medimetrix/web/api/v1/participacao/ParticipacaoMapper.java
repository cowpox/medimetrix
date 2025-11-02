package com.mmx.medimetrix.web.api.v1.participacao;

import com.mmx.medimetrix.application.participacao.commands.ParticipacaoCreate;
import com.mmx.medimetrix.application.participacao.commands.ParticipacaoUpdate;
import com.mmx.medimetrix.domain.core.Participacao;
import com.mmx.medimetrix.web.api.v1.participacao.dto.ParticipacaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.participacao.dto.ParticipacaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.participacao.dto.ParticipacaoUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class ParticipacaoMapper {

    public ParticipacaoCreate toCommand(ParticipacaoCreateDTO dto) {
        ParticipacaoCreate cmd = new ParticipacaoCreate();
        cmd.setIdAvaliacao(dto.getIdAvaliacao());
        cmd.setAvaliadoMedicoId(dto.getAvaliadoMedicoId());
        cmd.setUnidadeSnapshotId(dto.getUnidadeSnapshotId());
        cmd.setEspecialidadeSnapshotId(dto.getEspecialidadeSnapshotId());
        cmd.setStatus(dto.getStatus());
        return cmd;
    }

    public ParticipacaoUpdate toCommand(ParticipacaoUpdateDTO dto, Participacao atual) {
        ParticipacaoUpdate cmd = new ParticipacaoUpdate();
        cmd.setAvaliadoMedicoId(dto.getAvaliadoMedicoId() != null ? dto.getAvaliadoMedicoId() : atual.getAvaliadoMedicoId());
        cmd.setUnidadeSnapshotId(dto.getUnidadeSnapshotId() != null ? dto.getUnidadeSnapshotId() : atual.getUnidadeSnapshotId());
        cmd.setEspecialidadeSnapshotId(dto.getEspecialidadeSnapshotId() != null ? dto.getEspecialidadeSnapshotId() : atual.getEspecialidadeSnapshotId());
        cmd.setStatus(dto.getStatus() != null ? dto.getStatus() : atual.getStatus());
        return cmd;
    }

    public ParticipacaoResponseDTO toDTO(Participacao p) {
        ParticipacaoResponseDTO dto = new ParticipacaoResponseDTO();
        dto.setIdParticipacao(p.getIdParticipacao());
        dto.setIdAvaliacao(p.getIdAvaliacao());
        dto.setAvaliadoMedicoId(p.getAvaliadoMedicoId());
        dto.setUnidadeSnapshotId(p.getUnidadeSnapshotId());
        dto.setEspecialidadeSnapshotId(p.getEspecialidadeSnapshotId());
        dto.setStatus(p.getStatus());
        dto.setStartedAt(p.getStartedAt());
        dto.setLastActivityAt(p.getLastActivityAt());
        dto.setSubmittedAt(p.getSubmittedAt());
        dto.setDataCriacao(p.getDataCriacao());
        dto.setDataUltimaEdicao(p.getDataUltimaEdicao());
        return dto;
    }
}

package com.mmx.medimetrix.web.api.v1.participacao.dto;

import java.time.LocalDateTime;

public class ParticipacaoResponseDTO {
    private Long idParticipacao;
    private Long idAvaliacao;
    private Long avaliadoMedicoId;
    private Long unidadeSnapshotId;
    private Long especialidadeSnapshotId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime lastActivityAt;
    private LocalDateTime submittedAt;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaEdicao;

    public Long getIdParticipacao() { return idParticipacao; }
    public void setIdParticipacao(Long idParticipacao) { this.idParticipacao = idParticipacao; }

    public Long getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Long idAvaliacao) { this.idAvaliacao = idAvaliacao; }

    public Long getAvaliadoMedicoId() { return avaliadoMedicoId; }
    public void setAvaliadoMedicoId(Long avaliadoMedicoId) { this.avaliadoMedicoId = avaliadoMedicoId; }

    public Long getUnidadeSnapshotId() { return unidadeSnapshotId; }
    public void setUnidadeSnapshotId(Long unidadeSnapshotId) { this.unidadeSnapshotId = unidadeSnapshotId; }

    public Long getEspecialidadeSnapshotId() { return especialidadeSnapshotId; }
    public void setEspecialidadeSnapshotId(Long especialidadeSnapshotId) { this.especialidadeSnapshotId = especialidadeSnapshotId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }
}

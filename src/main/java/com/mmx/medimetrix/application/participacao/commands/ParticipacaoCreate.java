package com.mmx.medimetrix.application.participacao.commands;

public class ParticipacaoCreate {
    private Long idAvaliacao;
    private Long avaliadoMedicoId;
    private Long unidadeSnapshotId;       // opcional
    private Long especialidadeSnapshotId; // opcional
    private String status;                // PENDENTE | EM_ANDAMENTO | RESPONDIDA

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
}

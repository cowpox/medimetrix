package com.mmx.medimetrix.domain.core;

import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.PARTICIPACAO (V13__create_participacao.sql)
 */
public class Participacao {

    private Long idParticipacao;           // BIGSERIAL PK
    private Long idAvaliacao;              // NOT NULL FK -> AVALIACAO
    private Long avaliadoMedicoId;         // NOT NULL FK -> MEDICO(USUARIO_ID) (ativada no V16)

    // Snapshots para auditoria/relatórios
    private Long unidadeSnapshotId;        // FK -> UNIDADE (ativada no V15)
    private Long especialidadeSnapshotId;  // FK -> ESPECIALIDADE (ativada no V15)

    // Status e marcos de execução
    private String status;                 // 'PENDENTE' | 'EM_ANDAMENTO' | 'RESPONDIDA'
    private LocalDateTime startedAt;
    private LocalDateTime lastActivityAt;
    private LocalDateTime submittedAt;

    // Auditoria
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaEdicao;

    public Participacao() {}

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

    @Override
    public String toString() {
        return "Participacao{" +
                "idParticipacao=" + idParticipacao +
                ", idAvaliacao=" + idAvaliacao +
                ", avaliadoMedicoId=" + avaliadoMedicoId +
                ", unidadeSnapshotId=" + unidadeSnapshotId +
                ", especialidadeSnapshotId=" + especialidadeSnapshotId +
                ", status='" + status + '\'' +
                ", startedAt=" + startedAt +
                ", lastActivityAt=" + lastActivityAt +
                ", submittedAt=" + submittedAt +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

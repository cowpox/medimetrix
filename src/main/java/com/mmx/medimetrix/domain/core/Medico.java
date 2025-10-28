package com.mmx.medimetrix.domain.core;

import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.MEDICO (V9__create_medico.sql)
 * Subtipo 1:1 de USUARIO (PK = USUARIO_ID)
 */
public class Medico {

    // PK e FK para USUARIO(ID_USUARIO)
    private Long usuarioId;

    // FKs obrigatórias
    private Long especialidadeId;
    private Long unidadeId;

    // CRM
    private String crmNumero;   // VARCHAR(20) NOT NULL
    private String crmUf;       // CHAR(2) NOT NULL

    // Auditoria
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaEdicao;

    public Medico() {}

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getEspecialidadeId() { return especialidadeId; }
    public void setEspecialidadeId(Long especialidadeId) { this.especialidadeId = especialidadeId; }

    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }

    public String getCrmNumero() { return crmNumero; }
    public void setCrmNumero(String crmNumero) { this.crmNumero = crmNumero; }

    public String getCrmUf() { return crmUf; }
    public void setCrmUf(String crmUf) { this.crmUf = crmUf; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaEdicao() { return dataUltimaEdicao; }
    public void setDataUltimaEdicao(LocalDateTime dataUltimaEdicao) { this.dataUltimaEdicao = dataUltimaEdicao; }

    @Override
    public String toString() {
        return "Medico{" +
                "usuarioId=" + usuarioId +
                ", especialidadeId=" + especialidadeId +
                ", unidadeId=" + unidadeId +
                ", crmNumero='" + crmNumero + '\'' +
                ", crmUf='" + crmUf + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataUltimaEdicao=" + dataUltimaEdicao +
                '}';
    }
}

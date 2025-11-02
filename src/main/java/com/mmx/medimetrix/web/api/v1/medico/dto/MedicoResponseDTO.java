package com.mmx.medimetrix.web.api.v1.medico.dto;

import java.time.LocalDateTime;

public class MedicoResponseDTO {
    private Long usuarioId;
    private Long especialidadeId;
    private Long unidadeId;
    private String crmNumero;
    private String crmUf;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaEdicao;

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
}

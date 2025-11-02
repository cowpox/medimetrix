package com.mmx.medimetrix.web.api.v1.medico.dto;

import jakarta.validation.constraints.Size;

public class MedicoUpdateDTO {
    private Long especialidadeId;
    private Long unidadeId;

    @Size(max = 20)
    private String crmNumero;

    @Size(min = 2, max = 2)
    private String crmUf;

    public Long getEspecialidadeId() { return especialidadeId; }
    public void setEspecialidadeId(Long especialidadeId) { this.especialidadeId = especialidadeId; }

    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }

    public String getCrmNumero() { return crmNumero; }
    public void setCrmNumero(String crmNumero) { this.crmNumero = crmNumero; }

    public String getCrmUf() { return crmUf; }
    public void setCrmUf(String crmUf) { this.crmUf = crmUf; }
}

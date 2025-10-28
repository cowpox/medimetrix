package com.mmx.medimetrix.domain.core;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO alinhado a MEDIMETRIX.RESPOSTA (V14__create_resposta.sql)
 */
public class Resposta {

    private Long idResposta;          // BIGSERIAL PK

    private Long idParticipacao;      // NOT NULL FK -> PARTICIPACAO
    private Long idAvaliacao;         // NOT NULL FK -> AVALIACAO
    private Long idQuestao;           // NOT NULL FK -> QUESTAO

    // valores possíveis por tipo de questão
    private BigDecimal valorNumerico; // NUMERIC(10,3)
    private Boolean valorBinario;     // BOOLEAN
    private String texto;             // VARCHAR(2000)

    private LocalDateTime dataCriacao;  // TIMESTAMP NOT NULL

    public Resposta() {}

    public Long getIdResposta() { return idResposta; }
    public void setIdResposta(Long idResposta) { this.idResposta = idResposta; }

    public Long getIdParticipacao() { return idParticipacao; }
    public void setIdParticipacao(Long idParticipacao) { this.idParticipacao = idParticipacao; }

    public Long getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Long idAvaliacao) { this.idAvaliacao = idAvaliacao; }

    public Long getIdQuestao() { return idQuestao; }
    public void setIdQuestao(Long idQuestao) { this.idQuestao = idQuestao; }

    public BigDecimal getValorNumerico() { return valorNumerico; }
    public void setValorNumerico(BigDecimal valorNumerico) { this.valorNumerico = valorNumerico; }

    public Boolean getValorBinario() { return valorBinario; }
    public void setValorBinario(Boolean valorBinario) { this.valorBinario = valorBinario; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    @Override
    public String toString() {
        return "Resposta{" +
                "idResposta=" + idResposta +
                ", idParticipacao=" + idParticipacao +
                ", idAvaliacao=" + idAvaliacao +
                ", idQuestao=" + idQuestao +
                ", valorNumerico=" + valorNumerico +
                ", valorBinario=" + valorBinario +
                ", texto='" + texto + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}

package com.mmx.medimetrix.web.mvc.controller.gestor;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class CriterioForm {

    private Long id;

    @NotBlank
    private String nome;

    private String definicaoOperacional;
    private String descricao;

    private BigDecimal peso = BigDecimal.ONE;
    private Integer ordemSugerida;
    private Boolean ativo = Boolean.TRUE;

    private Integer versao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDefinicaoOperacional() { return definicaoOperacional; }
    public void setDefinicaoOperacional(String definicaoOperacional) {
        this.definicaoOperacional = definicaoOperacional;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getOrdemSugerida() { return ordemSugerida; }
    public void setOrdemSugerida(Integer ordemSugerida) { this.ordemSugerida = ordemSugerida; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }
}

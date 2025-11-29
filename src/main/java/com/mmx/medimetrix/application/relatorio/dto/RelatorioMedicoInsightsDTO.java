package com.mmx.medimetrix.application.relatorios.dto;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioMedicoInsightsDTO {

    private DadosMedico medico;
    private DadosUnidade unidade;
    private DadosAvaliacao avaliacao;
    private List<CriterioDesempenho> criterios;

    // getters / setters

    public static class DadosMedico {
        private Long idMedico;
        private String nome;
        private String especialidade;
        // getters / setters
    }

    public static class DadosUnidade {
        private Long idUnidade;
        private String nome;
        // getters / setters
    }

    public static class DadosAvaliacao {
        private Long idAvaliacao;
        private String titulo;
        private String periodo;           // ex: "01/06/2025 a 30/06/2025"
        private BigDecimal notaGlobalMedico;
        private BigDecimal notaMediaGrupo;
        private BigDecimal notaMinGrupo;
        private BigDecimal notaMaxGrupo;
        // getters / setters
    }

    public static class CriterioDesempenho {
        private String nomeCriterio;
        private BigDecimal notaMedico;    // 0–5
        private BigDecimal notaGrupo;     // 0–5
        // se quiser: private Integer quantidadeRespostas;
        // getters / setters
    }
}

package com.mmx.medimetrix.application.relatorio.vm;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contexto que será enviado para a LLM (ou usado pela heurística local)
 * com os dados essenciais do médico, da avaliação e das notas por critério.
 */
public record RelatorioMedicoInsightsRequestVM(
        Long idAvaliacao,
        Long idMedico,
        String tituloAvaliacao,
        String periodo,
        String statusAvaliacao,
        String nomeMedico,
        String nomeEspecialidade,
        String nomeUnidade,
        BigDecimal notaMedicoGlobal,  // média global do médico (0–5)
        BigDecimal notaGrupoGlobal,   // média global do grupo (0–5)
        List<CriterioRadarVM> criterios
) {}

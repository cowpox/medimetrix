package com.mmx.medimetrix.application.meta.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetaCreate(Long idCriterio, Long idUnidade, Long idEspecialidade,
                         BigDecimal alvo, String operador,
                         LocalDate vigenciaInicio, LocalDate vigenciaFim,
                         Integer prioridade, String justificativa) {}

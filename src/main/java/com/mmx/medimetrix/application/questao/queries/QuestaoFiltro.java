package com.mmx.medimetrix.application.questao.queries;

public record QuestaoFiltro(String enunciadoLike, String tipo, Long idCriterio, Integer page, Integer size) {}

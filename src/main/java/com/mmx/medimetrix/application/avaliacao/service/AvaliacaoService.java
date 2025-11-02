package com.mmx.medimetrix.application.avaliacao.service;

import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoCreate;
import com.mmx.medimetrix.domain.core.Avaliacao;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoService {
    Long create(AvaliacaoCreate cmd);
    void update(Long id, AvaliacaoCreate cmd);
    Optional<Avaliacao> findById(Long id);

    // Listagens paginadas conforme DAO
    List<Avaliacao> listPaged(int page, int size);
    List<Avaliacao> listByStatus(String status, int page, int size);
    List<Avaliacao> searchByTituloLike(String termo, int page, int size);

    void ativar(Long id);     // -> reactivate
    void desativar(Long id);  // -> deactivate
    void delete(Long id);
}

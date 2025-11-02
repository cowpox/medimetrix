package com.mmx.medimetrix.web.api.v1.participacao;

import com.mmx.medimetrix.application.participacao.service.ParticipacaoService;
import com.mmx.medimetrix.domain.core.Participacao;
import com.mmx.medimetrix.web.api.v1.participacao.dto.ParticipacaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.participacao.dto.ParticipacaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.participacao.dto.ParticipacaoUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/participacoes")
public class ParticipacaoController {

    private final ParticipacaoService service;
    private final ParticipacaoMapper mapper;

    public ParticipacaoController(ParticipacaoService service, ParticipacaoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ParticipacaoCreateDTO dto) {
        Long id = service.create(mapper.toCommand(dto));
        return ResponseEntity.created(URI.create("/api/v1/participacoes/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody ParticipacaoUpdateDTO dto) {
        Participacao atual = service.findById(id).orElseThrow();
        service.update(id, mapper.toCommand(dto, atual));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipacaoResponseDTO> getById(@PathVariable Long id) {
        Participacao p = service.findById(id).orElseThrow();
        return ResponseEntity.ok(mapper.toDTO(p));
    }

    /**
     * Filtros suportados pelo DAO:
     * - ?avaliacaoId=...
     * - ?medicoId=...
     * - ?status=...
     * - ?avaliacaoId=...&medicoId=...  -> busca 1x1 (findByAvaliacaoAndMedico)
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "avaliacaoId") Long idAvaliacao,
            @RequestParam(required = false, name = "medicoId") Long medicoId,
            @RequestParam(required = false) String status
    ) {
        if (idAvaliacao != null && medicoId != null) {
            return service.findByAvaliacaoAndMedico(idAvaliacao, medicoId)
                    .<ResponseEntity<?>>map(p -> ResponseEntity.ok(mapper.toDTO(p)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        List<Participacao> result;
        if (idAvaliacao != null) {
            result = service.listByAvaliacao(idAvaliacao, page, size);
        } else if (medicoId != null) {
            result = service.listByMedico(medicoId, page, size);
        } else if (status != null && !status.isBlank()) {
            result = service.listByStatus(status, page, size);
        } else {
            result = service.listPaged(page, size);
        }
        return ResponseEntity.ok(result.stream().map(mapper::toDTO).toList());
    }

    // Transições de estado
    @PatchMapping("/{id}/start")
    public ResponseEntity<Void> markStarted(@PathVariable Long id) {
        service.markStarted(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/touch")
    public ResponseEntity<Void> touch(@PathVariable Long id) {
        service.touchActivity(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<Void> submit(@PathVariable Long id) {
        service.markSubmitted(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

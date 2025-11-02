package com.mmx.medimetrix.web.api.v1.avaliacao;

import com.mmx.medimetrix.application.avaliacao.service.AvaliacaoService;
import com.mmx.medimetrix.domain.core.Avaliacao;
import com.mmx.medimetrix.web.api.v1.avaliacao.dto.AvaliacaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.avaliacao.dto.AvaliacaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.avaliacao.dto.AvaliacaoUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/avaliacoes")
@Validated
public class AvaliacaoController {

    private final AvaliacaoService service;
    private final AvaliacaoMapper mapper;

    public AvaliacaoController(AvaliacaoService service, AvaliacaoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody AvaliacaoCreateDTO dto,
                                       UriComponentsBuilder uriBuilder) {
        Long id = service.create(mapper.toCommand(dto));
        URI location = uriBuilder.path("/api/v1/avaliacoes/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // PUT -> 204 No Content
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody AvaliacaoUpdateDTO dto) {
        Avaliacao existente = service.findById(id).orElseThrow();
        service.update(id, mapper.toCommand(dto, existente));
        return ResponseEntity.noContent().build();
    }

    // GET by id -> 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> getById(@PathVariable Long id) {
        Avaliacao a = service.findById(id).orElseThrow();
        return ResponseEntity.ok(mapper.toDTO(a));
    }

    /**
     * Listagem paginada com filtros:
     * - Sem parâmetros → listPaged(page,size)
     * - status=...      → listByStatus(status,page,size)
     * - titulo=...      → searchByTituloLike(titulo,page,size)
     * (Se vier status e titulo juntos, prioriza tituloLike)
     */
    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "titulo") String tituloLike
    ) {
        List<Avaliacao> result;
        if (tituloLike != null && !tituloLike.isBlank()) {
            result = service.searchByTituloLike(tituloLike, page, size);
        } else if (status != null && !status.isBlank()) {
            result = service.listByStatus(status, page, size);
        } else {
            result = service.listPaged(page, size);
        }
        return ResponseEntity.ok(result.stream().map(mapper::toDTO).toList());
    }

    // PATCH -> 204 No Content
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        service.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE -> 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

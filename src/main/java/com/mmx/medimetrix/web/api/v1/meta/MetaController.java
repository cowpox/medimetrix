package com.mmx.medimetrix.web.api.v1.meta;

import com.mmx.medimetrix.application.meta.commands.MetaCreate;
import com.mmx.medimetrix.application.meta.commands.MetaUpdate;
import com.mmx.medimetrix.application.meta.queries.MetaFiltro;
import com.mmx.medimetrix.application.meta.service.MetaService;
import com.mmx.medimetrix.web.api.v1.meta.dto.MetaCreateDTO;
import com.mmx.medimetrix.web.api.v1.meta.dto.MetaResponseDTO;
import com.mmx.medimetrix.web.api.v1.meta.dto.MetaUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metas")
@Validated
public class MetaController {

    private final MetaService service;

    public MetaController(MetaService service) {
        this.service = service;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody MetaCreateDTO dto,
                                       UriComponentsBuilder uriBuilder) {
        var created = service.create(new MetaCreate(
                dto.idCriterio(),
                dto.idUnidade(),
                dto.idEspecialidade(),
                dto.alvo(),
                dto.operador(),
                dto.vigenciaInicio(),
                dto.vigenciaFim(),
                dto.prioridade(),
                dto.justificativa()
        ));
        URI location = uriBuilder.path("/api/v1/metas/{id}")
                .buildAndExpand(created.getIdMeta())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // GET by id -> 200 OK ou 404
    @GetMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(MetaMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET list -> 200 OK
    @GetMapping
    public ResponseEntity<List<MetaResponseDTO>> list(
            @RequestParam(required = false) Long idCriterio,
            @RequestParam(required = false) Long idUnidade,
            @RequestParam(required = false) Long idEspecialidade,
            @RequestParam(required = false) LocalDate naData,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) Integer size
    ) {
        var filtro = new MetaFiltro(idCriterio, idUnidade, idEspecialidade, naData, page, size);
        var resp = service.list(filtro).stream()
                .map(MetaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    // PUT -> 204 No Content
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @Valid @RequestBody MetaUpdateDTO dto) {
        service.update(id, new MetaUpdate(
                dto.idCriterio(),
                dto.idUnidade(),
                dto.idEspecialidade(),
                dto.alvo(),
                dto.operador(),
                dto.vigenciaInicio(),
                dto.vigenciaFim(),
                dto.ativo(),
                dto.prioridade(),
                dto.justificativa()
        ));
        return ResponseEntity.noContent().build();
    }

    // DELETE (desativar) -> 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // POST ativar -> 204 No Content
    @PostMapping("/{id}/ativar")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }
}

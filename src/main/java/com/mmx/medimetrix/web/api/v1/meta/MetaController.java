
package com.mmx.medimetrix.web.api.v1.meta;

import com.mmx.medimetrix.application.meta.commands.MetaCreate;
import com.mmx.medimetrix.application.meta.queries.MetaFiltro;
import com.mmx.medimetrix.application.meta.service.MetaService;
import com.mmx.medimetrix.web.api.v1.meta.dto.MetaCreateDTO;
import com.mmx.medimetrix.web.api.v1.meta.dto.MetaResponseDTO;
import com.mmx.medimetrix.web.api.v1.meta.dto.MetaUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metas")
@Validated
public class MetaController {

    private final MetaService service;

    public MetaController(MetaService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<MetaResponseDTO> create(@Valid @RequestBody MetaCreateDTO dto) {
        var created = service.create(new MetaCreate(
                dto.idCriterio(), dto.idUnidade(), dto.idEspecialidade(),
                dto.alvo(), dto.operador(), dto.vigenciaInicio(), dto.vigenciaFim(),
                dto.prioridade(), dto.justificativa()
        ));
        return ResponseEntity.created(URI.create("/api/v1/metas/" + created.getIdMeta()))
                .body(MetaMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id).map(MetaMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MetaResponseDTO>> list(
            @RequestParam(required = false) Long idCriterio,
            @RequestParam(required = false) Long idUnidade,
            @RequestParam(required = false) Long idEspecialidade,
            @RequestParam(required = false) java.time.LocalDate naData,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        var filtro = new MetaFiltro(idCriterio, idUnidade, idEspecialidade, naData, page, size);
        var resp = service.list(filtro).stream().map(MetaMapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MetaUpdateDTO dto) {
        var updated = service.update(id, dto.idCriterio(), dto.idUnidade(), dto.idEspecialidade(),
                dto.alvo(), dto.operador(), dto.vigenciaInicio(), dto.vigenciaFim(),
                dto.ativo(), dto.prioridade(), dto.justificativa());
        return ResponseEntity.ok(MetaMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ativar")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }
}


package com.mmx.medimetrix.web.api.v1.criterio;

import com.mmx.medimetrix.application.criterio.commands.CriterioCreate;
import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.application.criterio.service.CriterioService;
import com.mmx.medimetrix.web.api.v1.criterio.dto.CriterioCreateDTO;
import com.mmx.medimetrix.web.api.v1.criterio.dto.CriterioResponseDTO;
import com.mmx.medimetrix.web.api.v1.criterio.dto.CriterioUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/criterios")
@Validated
public class CriterioController {

    private final CriterioService service;

    public CriterioController(CriterioService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CriterioResponseDTO> create(@Valid @RequestBody CriterioCreateDTO dto) {
        var created = service.create(new CriterioCreate(dto.nome(), dto.definicaoOperacional(), dto.descricao()));
        return ResponseEntity.created(URI.create("/api/v1/criterios/" + created.getIdCriterio()))
                .body(CriterioMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterioResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id).map(CriterioMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CriterioResponseDTO>> list(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        var filtro = new CriterioFiltro(nome, page, size);
        var resp = service.list(filtro).stream().map(CriterioMapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CriterioUpdateDTO dto) {
        var updated = service.update(id, dto.nome(), dto.definicaoOperacional(), dto.descricao(), dto.ativo());
        return ResponseEntity.ok(CriterioMapper.toResponse(updated));
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

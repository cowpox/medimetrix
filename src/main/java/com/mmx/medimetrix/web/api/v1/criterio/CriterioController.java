package com.mmx.medimetrix.web.api.v1.criterio;

import com.mmx.medimetrix.application.criterio.commands.CriterioCreate;
import com.mmx.medimetrix.application.criterio.commands.CriterioUpdate;
import com.mmx.medimetrix.application.criterio.queries.CriterioFiltro;
import com.mmx.medimetrix.application.criterio.service.CriterioService;
import com.mmx.medimetrix.web.api.v1.criterio.dto.CriterioCreateDTO;
import com.mmx.medimetrix.web.api.v1.criterio.dto.CriterioResponseDTO;
import com.mmx.medimetrix.web.api.v1.criterio.dto.CriterioUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/criterios")
@Validated
public class CriterioController {

    private final CriterioService service;

    public CriterioController(CriterioService service) {
        this.service = service;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CriterioCreateDTO dto,
                                       UriComponentsBuilder uriBuilder) {
        var created = service.create(new CriterioCreate(
                dto.nome(),
                dto.definicaoOperacional(),
                dto.descricao()
        ));
        URI location = uriBuilder.path("/api/v1/criterios/{id}")
                .buildAndExpand(created.getIdCriterio())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // GET by id -> 200 OK ou 404
    @GetMapping("/{id}")
    public ResponseEntity<CriterioResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(CriterioMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET list -> 200 OK
    @GetMapping
    public ResponseEntity<List<CriterioResponseDTO>> list(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) Integer size
    ) {
        var filtro = new CriterioFiltro(nome, page, size);
        var resp = service.list(filtro).stream()
                .map(CriterioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    // PUT -> 204 No Content
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @Valid @RequestBody CriterioUpdateDTO dto) {
        service.update(id, new CriterioUpdate(
                dto.nome(),
                dto.definicaoOperacional(),
                dto.descricao(),
                dto.ativo()
        ));
    }


    // DELETE (desativar) -> 204 No Content
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }

    // POST ativar -> 204 No Content
    @PostMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Long id) {
        service.activate(id);
    }
}

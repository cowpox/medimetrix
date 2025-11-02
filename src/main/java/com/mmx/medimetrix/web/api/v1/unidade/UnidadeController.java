package com.mmx.medimetrix.web.api.v1.unidade;

import com.mmx.medimetrix.application.unidade.commands.UnidadeCreate;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;
import com.mmx.medimetrix.domain.core.Unidade;
import com.mmx.medimetrix.web.api.v1.unidade.dto.UnidadeCreateDTO;
import com.mmx.medimetrix.web.api.v1.unidade.dto.UnidadeResponseDTO;
import com.mmx.medimetrix.web.api.v1.unidade.dto.UnidadeUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/unidades")
@Validated
public class UnidadeController {

    private final UnidadeService service;

    public UnidadeController(UnidadeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UnidadeResponseDTO> create(@Valid @RequestBody UnidadeCreateDTO dto) {
        Unidade created = service.create(new UnidadeCreate(dto.nome()));
        return ResponseEntity
                .created(URI.create("/api/v1/unidades/" + created.getIdUnidade()))
                .body(UnidadeMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(UnidadeMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UnidadeResponseDTO>> list(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "nome") String sortBy,
            @RequestParam(required = false, defaultValue = "true") Boolean asc
    ) {
        var filtro = new UnidadeFiltro(nome, page, size, sortBy, asc);
        List<UnidadeResponseDTO> resp = service.list(filtro)
                .stream().map(UnidadeMapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadeResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UnidadeUpdateDTO dto) {
        Unidade updated = service.update(id, dto.nome(), dto.ativo());
        return ResponseEntity.ok(UnidadeMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }

    @PostMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Long id) {
        service.activate(id);
    }
}

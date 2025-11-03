package com.mmx.medimetrix.web.api.v1.unidade;

import com.mmx.medimetrix.application.unidade.commands.UnidadeCreate;
import com.mmx.medimetrix.application.unidade.commands.UnidadeUpdate;
import com.mmx.medimetrix.application.unidade.queries.UnidadeFiltro;
import com.mmx.medimetrix.application.unidade.service.UnidadeService;
import com.mmx.medimetrix.domain.core.Unidade;
import com.mmx.medimetrix.web.api.v1.unidade.dto.UnidadeCreateDTO;
import com.mmx.medimetrix.web.api.v1.unidade.dto.UnidadeResponseDTO;
import com.mmx.medimetrix.web.api.v1.unidade.dto.UnidadeUpdateDTO;
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
@RequestMapping("/api/v1/unidades")
@Validated
public class UnidadeController {

    private final UnidadeService service;

    public UnidadeController(UnidadeService service) {
        this.service = service;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UnidadeCreateDTO dto,
                                       UriComponentsBuilder uriBuilder) {
        Unidade created = service.create(new UnidadeCreate(dto.nome(), dto.gestorUsuarioId()));
        URI location = uriBuilder.path("/api/v1/unidades/{id}")
                .buildAndExpand(created.getIdUnidade())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // GET by id -> 200 OK ou 404
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(UnidadeMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET list -> 200 OK
    @GetMapping
    public ResponseEntity<List<UnidadeResponseDTO>> list(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) Integer size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc
    ) {
        var filtro = new UnidadeFiltro(nome, page, size, sortBy, asc);
        List<UnidadeResponseDTO> resp = service.list(filtro)
                .stream()
                .map(UnidadeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    // PUT -> 204 No Content
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @Valid @RequestBody UnidadeUpdateDTO dto) {
        // Validação simples: pelo menos um campo para alterar
        if (dto.nome() == null && dto.gestorUsuarioId() == null && dto.ativo() == null) {
            throw new IllegalArgumentException("Informe ao menos um campo para atualização.");
        }

        var cmd = new UnidadeUpdate(id, dto.nome(), dto.gestorUsuarioId(), dto.ativo());
        service.update(cmd); // usa o novo métoodo da service
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

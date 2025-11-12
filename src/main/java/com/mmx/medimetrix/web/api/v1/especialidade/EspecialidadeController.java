package com.mmx.medimetrix.web.api.v1.especialidade;

import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeCreate;
import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeUpdate;
import com.mmx.medimetrix.application.especialidade.queries.EspecialidadeFiltro;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.domain.core.Especialidade;
import com.mmx.medimetrix.web.api.v1.especialidade.dto.EspecialidadeCreateDTO;
import com.mmx.medimetrix.web.api.v1.especialidade.dto.EspecialidadeResponseDTO;
import com.mmx.medimetrix.web.api.v1.especialidade.dto.EspecialidadeUpdateDTO;
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
import org.springframework.util.StringUtils;


@RestController
@RequestMapping("/api/v1/especialidades")
@Validated
public class EspecialidadeController {

    private final EspecialidadeService service;

    public EspecialidadeController(EspecialidadeService service) {
        this.service = service;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody EspecialidadeCreateDTO dto,
                                       UriComponentsBuilder uriBuilder) {
        Especialidade created = service.create(new EspecialidadeCreate(dto.nome()));
        URI location = uriBuilder.path("/api/v1/especialidades/{id}")
                .buildAndExpand(created.getIdEspecialidade())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // GET by id -> 200 OK ou 404
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(EspecialidadeMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET list -> 200 OK
    @GetMapping
    public ResponseEntity<List<EspecialidadeResponseDTO>> list(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) Integer size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "true") Boolean asc
    ) {
        var filtro = new EspecialidadeFiltro(nome, page, size, sortBy, asc, null);
        List<EspecialidadeResponseDTO> resp = service.list(filtro)
                .stream()
                .map(EspecialidadeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    // PUT -> 204 No Content
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @Valid @RequestBody EspecialidadeUpdateDTO dto) {
        var cmd = new EspecialidadeUpdate(
                StringUtils.hasText(dto.nome()) ? dto.nome().trim() : null,
                dto.ativo()
        );
        service.update(id, cmd);
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

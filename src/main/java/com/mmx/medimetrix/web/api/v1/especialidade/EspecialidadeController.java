package com.mmx.medimetrix.web.api.v1.especialidade;

import com.mmx.medimetrix.application.especialidade.commands.EspecialidadeCreate;
import com.mmx.medimetrix.application.especialidade.queries.EspecialidadeFiltro;
import com.mmx.medimetrix.application.especialidade.service.EspecialidadeService;
import com.mmx.medimetrix.domain.core.Especialidade;
import com.mmx.medimetrix.web.api.v1.especialidade.dto.EspecialidadeCreateDTO;
import com.mmx.medimetrix.web.api.v1.especialidade.dto.EspecialidadeResponseDTO;
import com.mmx.medimetrix.web.api.v1.especialidade.dto.EspecialidadeUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/especialidades")
@Validated
public class EspecialidadeController {

    private final EspecialidadeService service;

    public EspecialidadeController(EspecialidadeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EspecialidadeResponseDTO> create(@Valid @RequestBody EspecialidadeCreateDTO dto) {
        Especialidade created = service.create(new EspecialidadeCreate(dto.nome()));
        return ResponseEntity
                .created(URI.create("/api/v1/especialidades/" + created.getIdEspecialidade()))
                .body(EspecialidadeMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(EspecialidadeMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponseDTO>> list(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "nome") String sortBy,
            @RequestParam(required = false, defaultValue = "true") Boolean asc
    ) {
        var filtro = new EspecialidadeFiltro(nome, page, size, sortBy, asc);
        List<EspecialidadeResponseDTO> resp = service.list(filtro)
                .stream().map(EspecialidadeMapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EspecialidadeUpdateDTO dto) {
        Especialidade updated = service.update(id, dto.nome(), dto.ativo());
        return ResponseEntity.ok(EspecialidadeMapper.toResponse(updated));
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

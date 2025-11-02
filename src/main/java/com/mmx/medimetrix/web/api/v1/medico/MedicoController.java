package com.mmx.medimetrix.web.api.v1.medico;

import com.mmx.medimetrix.application.medico.service.MedicoService;
import com.mmx.medimetrix.domain.core.Medico;
import com.mmx.medimetrix.web.api.v1.medico.dto.MedicoCreateDTO;
import com.mmx.medimetrix.web.api.v1.medico.dto.MedicoResponseDTO;
import com.mmx.medimetrix.web.api.v1.medico.dto.MedicoUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medicos")
public class MedicoController {

    private final MedicoService service;
    private final MedicoMapper mapper;

    public MedicoController(MedicoService service, MedicoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody MedicoCreateDTO dto) {
        Long usuarioId = service.create(mapper.toCommand(dto));
        return ResponseEntity.created(URI.create("/api/v1/medicos/" + usuarioId)).build();
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<Void> update(@PathVariable Long usuarioId, @Valid @RequestBody MedicoUpdateDTO dto) {
        Medico atual = service.findByUsuarioId(usuarioId).orElseThrow();
        service.update(usuarioId, mapper.toCommand(dto, atual));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<MedicoResponseDTO> getByUsuarioId(@PathVariable Long usuarioId) {
        return service.findByUsuarioId(usuarioId)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-crm")
    public ResponseEntity<MedicoResponseDTO> getByCrm(@RequestParam String numero, @RequestParam String uf) {
        return service.findByCrm(numero, uf)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Listagens:
     * - ?especialidadeId=...
     * - ?unidadeId=...
     * Sem filtros → página geral.
     */
    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long especialidadeId,
            @RequestParam(required = false) Long unidadeId
    ) {
        List<Medico> list;
        if (especialidadeId != null) {
            list = service.listByEspecialidade(especialidadeId, page, size);
        } else if (unidadeId != null) {
            list = service.listByUnidade(unidadeId, page, size);
        } else {
            list = service.listPaged(page, size);
        }
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> delete(@PathVariable Long usuarioId) {
        service.deleteByUsuarioId(usuarioId);
        return ResponseEntity.noContent().build();
    }
}

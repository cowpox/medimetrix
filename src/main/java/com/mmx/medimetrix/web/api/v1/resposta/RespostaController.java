package com.mmx.medimetrix.web.api.v1.resposta;

import com.mmx.medimetrix.application.resposta.service.RespostaService;
import com.mmx.medimetrix.domain.core.Resposta;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaCreateDTO;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaResponseDTO;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaUpdateValoresDTO;
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
@RequestMapping("/api/v1/respostas")
@Validated
public class RespostaController {

    private final RespostaService service;
    private final RespostaMapper mapper;

    public RespostaController(RespostaService service, RespostaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody RespostaCreateDTO dto,
                                       UriComponentsBuilder uriBuilder) {
        Long id = service.create(mapper.toCommand(dto));
        URI location = uriBuilder.path("/api/v1/respostas/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // POST sob participação -> 201 Created + Location, sem corpo
    @PostMapping("/participacoes/{idParticipacao}")
    public ResponseEntity<Void> createUnderParticipacao(@PathVariable Long idParticipacao,
                                                        @Valid @RequestBody RespostaCreateDTO dto,
                                                        UriComponentsBuilder uriBuilder) {
        dto.setIdParticipacao(idParticipacao);
        Long id = service.create(mapper.toCommand(dto));
        URI location = uriBuilder.path("/api/v1/respostas/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // PATCH -> 204 No Content
    @PatchMapping("/{idResposta}")
    public ResponseEntity<Void> updateValores(@PathVariable Long idResposta,
                                              @Valid @RequestBody RespostaUpdateValoresDTO dto) {
        service.updateValores(idResposta, mapper.toCommand(dto));
        return ResponseEntity.noContent().build();
    }

    // GET by id -> 200 OK ou 404
    @GetMapping("/{idResposta}")
    public ResponseEntity<RespostaResponseDTO> getById(@PathVariable Long idResposta) {
        return service.findById(idResposta)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET por participação -> 200 OK
    @GetMapping("/participacoes/{idParticipacao}")
    public ResponseEntity<List<RespostaResponseDTO>> listByParticipacao(@PathVariable Long idParticipacao) {
        List<Resposta> list = service.listByParticipacao(idParticipacao);
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    // GET geral/paginado -> 200 OK
    @GetMapping
    public ResponseEntity<List<RespostaResponseDTO>> listPaged(
            @RequestParam(required = false, name = "avaliacaoId") Long idAvaliacao,
            @RequestParam(required = false, name = "questaoId") Long idQuestao,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int size
    ) {
        if (idAvaliacao != null && idQuestao != null) {
            return ResponseEntity.badRequest().build(); // evita ambiguidade
        }
        List<Resposta> list;
        if (idAvaliacao != null) {
            list = service.listByAvaliacao(idAvaliacao, page, size);
        } else if (idQuestao != null) {
            list = service.listByQuestao(idQuestao, page, size);
        } else {
            list = service.listPaged(page, size);
        }
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    // DELETE por id -> 204 No Content
    @DeleteMapping("/{idResposta}")
    public ResponseEntity<Void> deleteById(@PathVariable Long idResposta) {
        service.deleteById(idResposta);
        return ResponseEntity.noContent().build();
    }

    // DELETE por participação -> 204 No Content
    @DeleteMapping("/participacoes/{idParticipacao}")
    public ResponseEntity<Void> deleteByParticipacao(@PathVariable Long idParticipacao) {
        service.deleteByParticipacao(idParticipacao);
        return ResponseEntity.noContent().build();
    }
}

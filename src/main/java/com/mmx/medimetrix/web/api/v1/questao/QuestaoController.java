package com.mmx.medimetrix.web.api.v1.questao;

import com.mmx.medimetrix.application.questao.commands.QuestaoCreate;
import com.mmx.medimetrix.application.questao.queries.QuestaoFiltro;
import com.mmx.medimetrix.application.questao.service.QuestaoService;
import com.mmx.medimetrix.web.api.v1.questao.dto.QuestaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.questao.dto.QuestaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.questao.dto.QuestaoUpdateDTO;
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
@RequestMapping("/api/v1/questoes")
@Validated
public class QuestaoController {

    private final QuestaoService service;

    public QuestaoController(QuestaoService service) {
        this.service = service;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody QuestaoCreateDTO dto,
                                       UriComponentsBuilder uriBuilder) {
        var created = service.create(new QuestaoCreate(
                dto.idCriterio(),
                dto.enunciado(),
                dto.tipo(),
                dto.obrigatoriedade(),
                dto.validacaoNumMin(),
                dto.validacaoNumMax(),
                dto.tamanhoTextoMax(),
                dto.sensivel(),
                dto.visivelParaGestor(),
                dto.ordemSugerida()
        ));
        URI location = uriBuilder.path("/api/v1/questoes/{id}")
                .buildAndExpand(created.getIdQuestao())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // GET by id -> 200 OK ou 404
    @GetMapping("/{id}")
    public ResponseEntity<QuestaoResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(QuestaoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET list -> 200 OK
    @GetMapping
    public ResponseEntity<List<QuestaoResponseDTO>> list(
            @RequestParam(required = false) String enunciado,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long idCriterio,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) Integer size
    ) {
        var filtro = new QuestaoFiltro(enunciado, tipo, idCriterio, page, size);
        var resp = service.list(filtro).stream()
                .map(QuestaoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    // PUT -> 204 No Content
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @Valid @RequestBody QuestaoUpdateDTO dto) {
        service.update(
                id,
                dto.enunciado(),
                dto.tipo(),
                dto.obrigatoriedade(),
                dto.validacaoNumMin(),
                dto.validacaoNumMax(),
                dto.tamanhoTextoMax(),
                dto.sensivel(),
                dto.visivelParaGestor(),
                dto.ativo(),
                dto.ordemSugerida()
        );
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

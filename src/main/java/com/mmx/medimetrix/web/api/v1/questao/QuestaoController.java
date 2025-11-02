
package com.mmx.medimetrix.web.api.v1.questao;

import com.mmx.medimetrix.application.questao.commands.QuestaoCreate;
import com.mmx.medimetrix.application.questao.queries.QuestaoFiltro;
import com.mmx.medimetrix.application.questao.service.QuestaoService;

import com.mmx.medimetrix.web.api.v1.questao.dto.QuestaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.questao.dto.QuestaoUpdateDTO;
import com.mmx.medimetrix.web.api.v1.questao.dto.QuestaoResponseDTO;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/questoes")
@Validated
public class QuestaoController {

    private final QuestaoService service;

    public QuestaoController(QuestaoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<QuestaoResponseDTO> create(@Valid @RequestBody QuestaoCreateDTO dto) {
        var created = service.create(new QuestaoCreate(
                dto.idCriterio(), dto.enunciado(),
                dto.tipo(), dto.obrigatoriedade(),
                dto.validacaoNumMin(), dto.validacaoNumMax(),
                dto.tamanhoTextoMax(), dto.sensivel(), dto.visivelParaGestor(), dto.ordemSugerida()
        ));
        return ResponseEntity.created(URI.create("/api/v1/questoes/" + created.getIdQuestao()))
                .body(QuestaoMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestaoResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id).map(QuestaoMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<QuestaoResponseDTO>> list(
            @RequestParam(required = false) String enunciado,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long idCriterio,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        var filtro = new QuestaoFiltro(enunciado, tipo, idCriterio, page, size);
        var resp = service.list(filtro).stream().map(QuestaoMapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestaoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody QuestaoUpdateDTO dto) {
        var updated = service.update(id, dto.enunciado(), dto.tipo(), dto.obrigatoriedade(),
                dto.validacaoNumMin(), dto.validacaoNumMax(), dto.tamanhoTextoMax(),
                dto.sensivel(), dto.visivelParaGestor(), dto.ativo(), dto.ordemSugerida());
        return ResponseEntity.ok(QuestaoMapper.toResponse(updated));
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

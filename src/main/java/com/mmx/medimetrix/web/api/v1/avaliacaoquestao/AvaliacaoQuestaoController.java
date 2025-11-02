package com.mmx.medimetrix.web.api.v1.avaliacaoquestao;

import com.mmx.medimetrix.application.avaliacaoquestao.service.AvaliacaoQuestaoService;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/avaliacoes/{idAvaliacao}/questoes")
@Validated
public class AvaliacaoQuestaoController {

    private final AvaliacaoQuestaoService service;
    private final AvaliacaoQuestaoMapper mapper;

    public AvaliacaoQuestaoController(AvaliacaoQuestaoService service, AvaliacaoQuestaoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> add(@PathVariable @Positive Long idAvaliacao,
                                    @Valid @RequestBody AvaliacaoQuestaoCreateDTO dto,
                                    UriComponentsBuilder uriBuilder) {
        service.addQuestao(idAvaliacao, mapper.toCommand(dto));
        // Se o DTO expõe o id da questão, podemos usá-lo para compor a Location:
        Long idQuestao = dto.getIdQuestao(); // ajuste para dto.idQuestao() se for record
        URI location = uriBuilder.path("/api/v1/avaliacoes/{idAvaliacao}/questoes/{idQuestao}")
                .buildAndExpand(idAvaliacao, idQuestao)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // PUT -> 204 No Content
    @PutMapping("/{idQuestao}")
    public ResponseEntity<Void> update(@PathVariable @Positive Long idAvaliacao,
                                       @PathVariable @Positive Long idQuestao,
                                       @Valid @RequestBody AvaliacaoQuestaoUpdateDTO dto) {
        service.update(idAvaliacao, idQuestao, mapper.toCommand(dto));
        return ResponseEntity.noContent().build();
    }

    // GET list -> 200 OK
    @GetMapping
    public ResponseEntity<List<AvaliacaoQuestaoResponseDTO>> list(@PathVariable @Positive Long idAvaliacao) {
        List<AvaliacaoQuestao> list = service.listByAvaliacao(idAvaliacao);
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    // GET one -> 200 OK ou 404
    @GetMapping("/{idQuestao}")
    public ResponseEntity<AvaliacaoQuestaoResponseDTO> getOne(@PathVariable @Positive Long idAvaliacao,
                                                              @PathVariable @Positive Long idQuestao) {
        return service.findOne(idAvaliacao, idQuestao)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE one -> 204 No Content
    @DeleteMapping("/{idQuestao}")
    public ResponseEntity<Void> deleteOne(@PathVariable @Positive Long idAvaliacao,
                                          @PathVariable @Positive Long idQuestao) {
        service.deleteOne(idAvaliacao, idQuestao);
        return ResponseEntity.noContent().build();
    }

    // DELETE all (quando ?all=true) -> 204 No Content, senão 400
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@PathVariable @Positive Long idAvaliacao,
                                          @RequestParam(name = "all", defaultValue = "false") boolean all) {
        if (!all) {
            return ResponseEntity.badRequest().build();
        }
        service.deleteAllByAvaliacao(idAvaliacao);
        return ResponseEntity.noContent().build();
    }

    // POST reordenar -> 204 No Content
    @PostMapping("/reordenar")
    public ResponseEntity<Void> swap(@PathVariable @Positive Long idAvaliacao,
                                     @RequestParam @Positive Long idQuestaoA,
                                     @RequestParam @Positive Long idQuestaoB) {
        service.swapOrdem(idAvaliacao, idQuestaoA, idQuestaoB);
        return ResponseEntity.noContent().build();
    }
}

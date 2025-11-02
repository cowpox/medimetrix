package com.mmx.medimetrix.web.api.v1.avaliacaoquestao;

import com.mmx.medimetrix.application.avaliacaoquestao.service.AvaliacaoQuestaoService;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoCreateDTO;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoResponseDTO;
import com.mmx.medimetrix.web.api.v1.avaliacaoquestao.dto.AvaliacaoQuestaoUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/avaliacoes/{idAvaliacao}/questoes")
public class AvaliacaoQuestaoController {

    private final AvaliacaoQuestaoService service;
    private final AvaliacaoQuestaoMapper mapper;

    public AvaliacaoQuestaoController(AvaliacaoQuestaoService service, AvaliacaoQuestaoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<Void> add(@PathVariable Long idAvaliacao,
                                    @Valid @RequestBody AvaliacaoQuestaoCreateDTO dto) {
        service.addQuestao(idAvaliacao, mapper.toCommand(dto));
        return ResponseEntity.created(URI.create("/api/v1/avaliacoes/" + idAvaliacao + "/questoes/" + dto.getIdQuestao())).build();
    }

    @PutMapping("/{idQuestao}")
    public ResponseEntity<Void> update(@PathVariable Long idAvaliacao,
                                       @PathVariable Long idQuestao,
                                       @Valid @RequestBody AvaliacaoQuestaoUpdateDTO dto) {
        service.update(idAvaliacao, idQuestao, mapper.toCommand(dto));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoQuestaoResponseDTO>> list(@PathVariable Long idAvaliacao) {
        List<AvaliacaoQuestao> list = service.listByAvaliacao(idAvaliacao);
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    @GetMapping("/{idQuestao}")
    public ResponseEntity<AvaliacaoQuestaoResponseDTO> getOne(@PathVariable Long idAvaliacao,
                                                              @PathVariable Long idQuestao) {
        AvaliacaoQuestao aq = service.findOne(idAvaliacao, idQuestao).orElseThrow();
        return ResponseEntity.ok(mapper.toDTO(aq));
    }

    @DeleteMapping("/{idQuestao}")
    public ResponseEntity<Void> deleteOne(@PathVariable Long idAvaliacao,
                                          @PathVariable Long idQuestao) {
        service.deleteOne(idAvaliacao, idQuestao);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@PathVariable Long idAvaliacao,
                                          @RequestParam(name = "all", defaultValue = "false") boolean all) {
        if (all) {
            service.deleteAllByAvaliacao(idAvaliacao);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/reordenar")
    public ResponseEntity<Void> swap(@PathVariable Long idAvaliacao,
                                     @RequestParam Long idQuestaoA,
                                     @RequestParam Long idQuestaoB) {
        service.swapOrdem(idAvaliacao, idQuestaoA, idQuestaoB);
        return ResponseEntity.noContent().build();
    }
}

package com.mmx.medimetrix.web.api.v1.resposta;

import com.mmx.medimetrix.application.resposta.service.RespostaService;
import com.mmx.medimetrix.domain.core.Resposta;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaCreateDTO;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaResponseDTO;
import com.mmx.medimetrix.web.api.v1.resposta.dto.RespostaUpdateValoresDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RespostaController {

    private final RespostaService service;
    private final RespostaMapper mapper;

    public RespostaController(RespostaService service, RespostaMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    // Criar (permitindo tanto rota geral quanto aninhada por participação)
    @PostMapping("/respostas")
    public ResponseEntity<Void> create(@Valid @RequestBody RespostaCreateDTO dto) {
        Long id = service.create(mapper.toCommand(dto));
        return ResponseEntity.created(URI.create("/api/v1/respostas/" + id)).build();
    }

    @PostMapping("/participacoes/{idParticipacao}/respostas")
    public ResponseEntity<Void> createUnderParticipacao(@PathVariable Long idParticipacao,
                                                        @Valid @RequestBody RespostaCreateDTO dto) {
        dto.setIdParticipacao(idParticipacao);
        Long id = service.create(mapper.toCommand(dto));
        return ResponseEntity.created(URI.create("/api/v1/respostas/" + id)).build();
    }

    // Atualizar valores
    @PatchMapping("/respostas/{idResposta}")
    public ResponseEntity<Void> updateValores(@PathVariable Long idResposta,
                                              @Valid @RequestBody RespostaUpdateValoresDTO dto) {
        service.updateValores(idResposta, mapper.toCommand(dto));
        return ResponseEntity.noContent().build();
    }

    // Buscar uma
    @GetMapping("/respostas/{idResposta}")
    public ResponseEntity<RespostaResponseDTO> getById(@PathVariable Long idResposta) {
        return service.findById(idResposta)
                .map(mapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Listar por participação (sem paginação no DAO)
    @GetMapping("/participacoes/{idParticipacao}/respostas")
    public ResponseEntity<List<RespostaResponseDTO>> listByParticipacao(@PathVariable Long idParticipacao) {
        List<Resposta> list = service.listByParticipacao(idParticipacao);
        return ResponseEntity.ok(list.stream().map(mapper::toDTO).toList());
    }

    // Listar por avaliação OU por questão (ambos paginados no DAO)
    @GetMapping("/respostas")
    public ResponseEntity<?> listPaged(
            @RequestParam(required = false, name = "avaliacaoId") Long idAvaliacao,
            @RequestParam(required = false, name = "questaoId") Long idQuestao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (idAvaliacao != null && idQuestao != null) {
            return ResponseEntity.badRequest().build(); // evite ambiguidade
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

    // Delete
    @DeleteMapping("/respostas/{idResposta}")
    public ResponseEntity<Void> deleteById(@PathVariable Long idResposta) {
        service.deleteById(idResposta);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/participacoes/{idParticipacao}/respostas")
    public ResponseEntity<Void> deleteByParticipacao(@PathVariable Long idParticipacao) {
        service.deleteByParticipacao(idParticipacao);
        return ResponseEntity.noContent().build();
    }
}

package com.mmx.medimetrix.web.api.v1.usuario;

import com.mmx.medimetrix.application.usuario.queries.UsuarioFiltro;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioUpdateDTO;
import com.mmx.medimetrix.domain.enums.Papel;
import com.mmx.medimetrix.domain.core.Usuario;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioCreateDTO;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioResponseDTO;
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
@RequestMapping("/api/v1/usuarios")
@Validated
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // POST -> 201 Created + Location, sem corpo
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UsuarioCreateDTO dto,
                                       UriComponentsBuilder uriBuilder){
        Usuario u = service.create(UsuarioMapper.toCreateCommand(dto));
        URI location = uriBuilder.path("/api/v1/usuarios/{id}")
                .buildAndExpand(u.getIdUsuario())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // PUT -> 204 No Content
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        service.update(UsuarioMapper.toUpdateCommand(id, dto));
    }

    // GET by id -> 200 OK + DTO
    @GetMapping("/{id}")
    public UsuarioResponseDTO getById(@PathVariable long id){
        return UsuarioMapper.toResponse(service.getById(id));
    }

    // GET search/list -> 200 OK + lista de DTOs
    @GetMapping
    public List<UsuarioResponseDTO> search(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Papel papel,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int size,
            @RequestParam(defaultValue = "NOME") String sort,
            @RequestParam(defaultValue = "true") boolean asc
    ){
        UsuarioFiltro filtro = new UsuarioFiltro(termo, papel, ativo);
        return service.search(filtro, page, size, sort, asc).stream()
                .map(UsuarioMapper::toResponse).toList();
    }

    // PATCH (desativar) -> 204 No Content
    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable long id){
        service.desativar(id);
    }

    // PATCH (reativar) -> 204 No Content
    @PatchMapping("/{id}/reativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reativar(@PathVariable long id){
        service.reativar(id);
    }

    // DELETE -> 204 No Content
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id){
        service.delete(id);
    }
}

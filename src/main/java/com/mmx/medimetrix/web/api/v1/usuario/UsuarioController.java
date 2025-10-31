package com.mmx.medimetrix.web.api.v1.usuario;

import com.mmx.medimetrix.application.usuario.queries.UsuarioFiltro;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.domain.enums.Papel;
import com.mmx.medimetrix.domain.core.Usuario;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioCreateDTO;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateDTO dto){
        Usuario u = service.create(UsuarioMapper.toCreateCommand(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponse(u));
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO getById(@PathVariable long id){
        return UsuarioMapper.toResponse(service.getById(id));
    }

    @GetMapping
    public List<UsuarioResponseDTO> search(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Papel papel,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "NOME") String sort,
            @RequestParam(defaultValue = "true") boolean asc
    ){
        UsuarioFiltro filtro = new UsuarioFiltro(termo, papel, ativo);
        return service.search(filtro, page, size, sort, asc).stream()
                .map(UsuarioMapper::toResponse).toList();
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable long id){
        service.desativar(id);
    }

    @PatchMapping("/{id}/reativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reativar(@PathVariable long id){
        service.reativar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id){
        service.delete(id);
    }
}

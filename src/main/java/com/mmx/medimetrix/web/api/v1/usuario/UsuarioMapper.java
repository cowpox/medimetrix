package com.mmx.medimetrix.web.api.v1.usuario;

import com.mmx.medimetrix.application.usuario.commands.UsuarioCreate;
import com.mmx.medimetrix.application.usuario.commands.UsuarioUpdate;
import com.mmx.medimetrix.domain.core.Usuario;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioCreateDTO;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioResponseDTO;
import com.mmx.medimetrix.web.api.v1.usuario.dto.UsuarioUpdateDTO;

public final class UsuarioMapper {
    private UsuarioMapper(){}

    public static UsuarioCreate toCreateCommand(UsuarioCreateDTO dto){
        return new UsuarioCreate(dto.nome(), dto.email(), dto.papel());
    }

    public static UsuarioUpdate toUpdateCommand(Long id, UsuarioUpdateDTO dto) {
        return new UsuarioUpdate(
                id,
                dto.nome(),
                dto.email(),
                dto.papel(),
                dto.ativo()
        );
    }

    public static UsuarioResponseDTO toResponse(Usuario u){
        return new UsuarioResponseDTO(
                u.getIdUsuario(),
                u.getNome(),
                u.getEmail(),
                u.getPapel(),
                u.getAtivo(),
                u.getDataCriacao(),
                u.getDataUltimaEdicao()
        );
    }
}

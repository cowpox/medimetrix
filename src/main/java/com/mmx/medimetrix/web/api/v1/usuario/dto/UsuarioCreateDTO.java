package com.mmx.medimetrix.web.api.v1.usuario.dto;

import com.mmx.medimetrix.domain.enums.Papel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 255) String email,
        @NotNull Papel papel
) {}

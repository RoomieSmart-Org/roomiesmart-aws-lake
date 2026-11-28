package com.roomiesmart.identity.infrastructure.adapter.in.rest.dto;

import com.roomiesmart.identity.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo invalido")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        String fullName,

        Role role
) {}

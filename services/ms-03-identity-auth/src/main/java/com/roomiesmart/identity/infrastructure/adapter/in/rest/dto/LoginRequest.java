package com.roomiesmart.identity.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo invalido")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {}

package com.roomiesmart.identity.domain.exception;

public class AuthException extends RuntimeException {
    private final String code;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AuthException emailAlreadyExists(String email) {
        return new AuthException("EMAIL_ALREADY_EXISTS", "El correo ya está registrado: " + email);
    }

    public static AuthException invalidCredentials() {
        return new AuthException("INVALID_CREDENTIALS", "Correo o contraseña incorrectos.");
    }

    public static AuthException invalidToken(String reason) {
        return new AuthException("INVALID_TOKEN", "Token inválido: " + reason);
    }
}
package com.roomiesmart.identity.infrastructure.adapter.in.rest;

import com.roomiesmart.identity.domain.exception.AuthException;
import com.roomiesmart.identity.domain.model.AuthTokens;
import com.roomiesmart.identity.domain.model.TokenClaims;
import com.roomiesmart.identity.domain.port.in.AuthUseCase;
import com.roomiesmart.identity.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.roomiesmart.identity.infrastructure.adapter.in.rest.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokens> register(@Valid @RequestBody RegisterRequest request) {
        AuthTokens tokens = authUseCase.register(
                request.email(),
                request.password(),
                request.fullName(),
                request.role()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokens> login(@Valid @RequestBody LoginRequest request) {
        AuthTokens tokens = authUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenClaims> validate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        TokenClaims claims = authUseCase.validateToken(extractBearerToken(authHeader));
        return ResponseEntity.ok(claims);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        authUseCase.logout(extractBearerToken(authHeader));
        return ResponseEntity.noContent().build();
    }

    private String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw AuthException.invalidToken("Falta el encabezado Authorization con formato Bearer.");
        }
        return authHeader.substring(7).trim();
    }
}

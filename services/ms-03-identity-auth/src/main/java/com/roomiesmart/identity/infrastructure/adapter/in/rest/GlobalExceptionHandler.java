package com.roomiesmart.identity.infrastructure.adapter.in.rest;

import com.roomiesmart.identity.domain.exception.AuthException;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        HttpStatus status = "EMAIL_ALREADY_EXISTS".equals(ex.getCode())
                ? HttpStatus.CONFLICT
                : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).body(Map.of(
                "error", ex.getCode(),
                "message", ex.getMessage(),
                "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Valor invalido",
                        (a, b) -> a
                ));

        return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "details", fieldErrors,
                "timestamp", Instant.now().toString()
        ));
    }
}
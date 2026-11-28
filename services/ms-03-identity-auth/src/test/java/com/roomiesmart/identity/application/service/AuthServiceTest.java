package com.roomiesmart.identity.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roomiesmart.identity.domain.exception.AuthException;
import com.roomiesmart.identity.domain.model.Role;
import com.roomiesmart.identity.domain.model.TokenClaims;
import com.roomiesmart.identity.domain.model.User;
import com.roomiesmart.identity.domain.port.out.PasswordEncoderPort;
import com.roomiesmart.identity.domain.port.out.SessionStorePort;
import com.roomiesmart.identity.domain.port.out.TokenProviderPort;
import com.roomiesmart.identity.domain.port.out.UserRepositoryPort;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;
    @Mock
    private TokenProviderPort tokenProvider;
    @Mock
    private SessionStorePort sessionStore;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, tokenProvider, sessionStore);
    }

    @Test
    void registerShouldCreateUserAndSaveRedisSession() {
        when(userRepository.existsByEmail("ana@roomiesmart.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.generateAccessToken(any(User.class))).thenReturn("access-jwt");
        when(tokenProvider.generateRefreshToken(any(User.class))).thenReturn("refresh-jwt");
        when(tokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
        when(tokenProvider.getRefreshTokenExpirationSeconds()).thenReturn(604800L);

        var tokens = authService.register("  ANA@RoomieSmart.com ", "Secret123!", "Ana Perez", Role.TENANT);

        assertNotNull(tokens);
        assertEquals("access-jwt", tokens.accessToken());
        assertEquals("Bearer", tokens.tokenType());
        verify(sessionStore).saveActiveSession(any(String.class), eq("refresh-jwt"), eq(604800L));
    }

    @Test
    void registerShouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("ana@roomiesmart.com")).thenReturn(true);

        var ex = assertThrows(AuthException.class, () ->
                authService.register("ana@roomiesmart.com", "Secret123!", "Ana Perez", Role.TENANT));

        assertEquals("EMAIL_ALREADY_EXISTS", ex.getCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginShouldReturnTokensWhenCredentialsAreValid() {
        var existingUser = new User("u-1", "ana@roomiesmart.com", "$2a$10$hashed", "Ana", Role.LANDLORD, Instant.now());
        when(userRepository.findByEmail("ana@roomiesmart.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("Secret123!", "$2a$10$hashed")).thenReturn(true);
        when(tokenProvider.generateAccessToken(existingUser)).thenReturn("access-jwt");
        when(tokenProvider.generateRefreshToken(existingUser)).thenReturn("refresh-jwt");
        when(tokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);
        when(tokenProvider.getRefreshTokenExpirationSeconds()).thenReturn(604800L);

        var tokens = authService.login("ana@roomiesmart.com", "Secret123!");

        assertEquals("access-jwt", tokens.accessToken());
        verify(sessionStore).saveActiveSession("u-1", "refresh-jwt", 604800L);
    }

    @Test
    void loginShouldThrowWhenPasswordIsWrong() {
        var existingUser = new User("u-1", "ana@roomiesmart.com", "$2a$10$hashed", "Ana", Role.TENANT, Instant.now());
        when(userRepository.findByEmail("ana@roomiesmart.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("WrongPass", "$2a$10$hashed")).thenReturn(false);

        var ex = assertThrows(AuthException.class, () ->
                authService.login("ana@roomiesmart.com", "WrongPass"));

        assertEquals("INVALID_CREDENTIALS", ex.getCode());
    }

    @Test
    void validateTokenShouldRejectBlacklistedToken() {
        when(sessionStore.isTokenBlacklisted("revoked-jwt")).thenReturn(true);

        var ex = assertThrows(AuthException.class, () -> authService.validateToken("revoked-jwt"));

        assertEquals("INVALID_TOKEN", ex.getCode());
        verify(tokenProvider, never()).parseAndValidate(any());
    }

    @Test
    void logoutShouldBlacklistTokenAndRevokeSession() {
        var claims = new TokenClaims("u-1", "ana@roomiesmart.com", Role.TENANT, true);
        when(sessionStore.isTokenBlacklisted("valid-jwt")).thenReturn(false);
        when(tokenProvider.parseAndValidate("valid-jwt")).thenReturn(claims);
        when(tokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);

        authService.logout("valid-jwt");

        verify(sessionStore).blacklistAccessToken("valid-jwt", 3600L);
        verify(sessionStore).revokeUserSession("u-1");
    }
}
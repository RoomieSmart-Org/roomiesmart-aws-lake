package com.roomiesmart.identity.application.service;

import com.roomiesmart.identity.domain.exception.AuthException;
import com.roomiesmart.identity.domain.model.AuthTokens;
import com.roomiesmart.identity.domain.model.Role;
import com.roomiesmart.identity.domain.model.TokenClaims;
import com.roomiesmart.identity.domain.model.User;
import com.roomiesmart.identity.domain.port.in.AuthUseCase;
import com.roomiesmart.identity.domain.port.out.PasswordEncoderPort;
import com.roomiesmart.identity.domain.port.out.SessionStorePort;
import com.roomiesmart.identity.domain.port.out.TokenProviderPort;
import com.roomiesmart.identity.domain.port.out.UserRepositoryPort;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final SessionStorePort sessionStore;

    public AuthService(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider,
            SessionStorePort sessionStore) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.sessionStore = sessionStore;
    }

    @Override
    public AuthTokens register(String email, String rawPassword, String fullName, Role role) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw AuthException.emailAlreadyExists(normalizedEmail);
        }

        User newUser = new User(
                UUID.randomUUID().toString(),
                normalizedEmail,
                passwordEncoder.encode(rawPassword),
                fullName.trim(),
                role != null ? role : Role.TENANT,
                Instant.now()
        );

        User savedUser = userRepository.save(newUser);
        return issueTokensAndPersistSession(savedUser);
    }

    @Override
    public AuthTokens login(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(AuthException::invalidCredentials);

        if (!passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw AuthException.invalidCredentials();
        }

        return issueTokensAndPersistSession(user);
    }

    @Override
    public TokenClaims validateToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw AuthException.invalidToken("Token vacío.");
        }
        if (sessionStore.isTokenBlacklisted(accessToken)) {
            throw AuthException.invalidToken("La sesión fue cerrada (token revocado).");
        }
        return tokenProvider.parseAndValidate(accessToken);
    }

    @Override
    public void logout(String accessToken) {
        TokenClaims claims = validateToken(accessToken);
        sessionStore.blacklistAccessToken(accessToken, tokenProvider.getAccessTokenExpirationSeconds());
        sessionStore.revokeUserSession(claims.userId());
    }

    private AuthTokens issueTokensAndPersistSession(User user) {
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        sessionStore.saveActiveSession(
                user.id(),
                refreshToken,
                tokenProvider.getRefreshTokenExpirationSeconds()
        );

        return AuthTokens.bearer(
                accessToken,
                refreshToken,
                tokenProvider.getAccessTokenExpirationSeconds()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
package com.roomiesmart.identity.infrastructure.adapter.out.security;

import com.roomiesmart.identity.domain.exception.AuthException;
import com.roomiesmart.identity.domain.model.Role;
import com.roomiesmart.identity.domain.model.TokenClaims;
import com.roomiesmart.identity.domain.model.User;
import com.roomiesmart.identity.domain.port.out.TokenProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final SecretKey secretKey;
    private final long accessExpirationSeconds;
    private final long refreshExpirationSeconds;

    public JwtTokenProviderAdapter(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.access-token-expiration-seconds:3600}") long accessExpirationSeconds,
            @Value("${security.jwt.refresh-token-expiration-seconds:604800}") long refreshExpirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
    }

    @Override
    public String generateAccessToken(User user) {
        return buildToken(user, accessExpirationSeconds, "ACCESS");
    }

    @Override
    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationSeconds, "REFRESH");
    }

    @Override
    public TokenClaims parseAndValidate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            Role role = Role.valueOf(claims.get("role", String.class));

            return new TokenClaims(userId, email, role, true);
        } catch (JwtException | IllegalArgumentException ex) {
            throw AuthException.invalidToken(ex.getMessage());
        }
    }

    @Override
    public long getAccessTokenExpirationSeconds() {
        return accessExpirationSeconds;
    }

    @Override
    public long getRefreshTokenExpirationSeconds() {
        return refreshExpirationSeconds;
    }

    private String buildToken(User user, long ttlSeconds, String tokenType) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.id())
                .claim("email", user.email())
                .claim("role", user.role().name())
                .claim("type", tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(secretKey)
                .compact();
    }
}

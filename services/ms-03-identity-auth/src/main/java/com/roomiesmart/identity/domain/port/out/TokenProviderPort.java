package com.roomiesmart.identity.domain.port.out;

import com.roomiesmart.identity.domain.model.TokenClaims;
import com.roomiesmart.identity.domain.model.User;

public interface TokenProviderPort {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    TokenClaims parseAndValidate(String token);
    long getAccessTokenExpirationSeconds();
    long getRefreshTokenExpirationSeconds();
}
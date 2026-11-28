package com.roomiesmart.identity.domain.port.in;

import com.roomiesmart.identity.domain.model.AuthTokens;
import com.roomiesmart.identity.domain.model.Role;
import com.roomiesmart.identity.domain.model.TokenClaims;

public interface AuthUseCase {
    AuthTokens register(String email, String rawPassword, String fullName, Role role);
    AuthTokens login(String email, String rawPassword);
    TokenClaims validateToken(String accessToken);
    void logout(String accessToken);
}
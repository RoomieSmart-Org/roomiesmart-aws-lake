package com.roomiesmart.identity.domain.port.out;

public interface SessionStorePort {
    void saveActiveSession(String userId, String refreshToken, long ttlSeconds);
    void revokeUserSession(String userId);
    void blacklistAccessToken(String accessToken, long ttlSeconds);
    boolean isTokenBlacklisted(String accessToken);
}
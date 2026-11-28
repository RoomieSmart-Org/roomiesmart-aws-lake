package com.roomiesmart.identity.infrastructure.adapter.out.redis;

import com.roomiesmart.identity.domain.port.out.SessionStorePort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSessionStoreAdapter implements SessionStorePort {

    private static final Logger log = LoggerFactory.getLogger(RedisSessionStoreAdapter.class);
    private static final String SESSION_PREFIX = "roomiesmart:session:";
    private static final String BLACKLIST_PREFIX = "roomiesmart:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public RedisSessionStoreAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @CircuitBreaker(name = "defaultService", fallbackMethod = "fallbackSaveSession")
    @Retry(name = "defaultService")
    public void saveActiveSession(String userId, String refreshToken, long ttlSeconds) {
        redisTemplate.opsForValue().set(SESSION_PREFIX + userId, refreshToken, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    @CircuitBreaker(name = "defaultService", fallbackMethod = "fallbackRevokeSession")
    @Retry(name = "defaultService")
    public void revokeUserSession(String userId) {
        redisTemplate.delete(SESSION_PREFIX + userId);
    }

    @Override
    @CircuitBreaker(name = "defaultService", fallbackMethod = "fallbackBlacklistToken")
    @Retry(name = "defaultService")
    public void blacklistAccessToken(String accessToken, long ttlSeconds) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, "revoked", Duration.ofSeconds(ttlSeconds));
    }

    @Override
    @CircuitBreaker(name = "defaultService", fallbackMethod = "fallbackIsBlacklisted")
    @Retry(name = "defaultService")
    public boolean isTokenBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken));
    }

    private void fallbackSaveSession(String userId, String refreshToken, long ttlSeconds, Throwable ex) {
        log.warn("Redis no disponible al guardar sesion para {}: {}", userId, ex.getMessage());
    }

    private void fallbackRevokeSession(String userId, Throwable ex) {
        log.warn("Redis no disponible al revocar sesion para {}: {}", userId, ex.getMessage());
    }

    private void fallbackBlacklistToken(String accessToken, long ttlSeconds, Throwable ex) {
        log.warn("Redis no disponible al invalidar token: {}", ex.getMessage());
    }

    private boolean fallbackIsBlacklisted(String accessToken, Throwable ex) {
        log.warn("Redis no disponible al verificar blacklist (fail-open temporal): {}", ex.getMessage());
        return false;
    }
}

package ru.ivanov.authservice.service;

import ru.ivanov.authservice.model.RefreshToken;
import ru.ivanov.authservice.model.enums.RefreshTokenStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    void save(RefreshToken refreshToken);

    boolean existsByToken(String token);

    boolean isTokenRevoked(String token);

    RefreshTokenStatus getTokenStatus(String token);

    void rotateToken(String oldToken, RefreshToken newToken);

    void revokeAllUserTokens(UUID userId);

    Optional<RefreshToken> findByToken(String token);

    void cleanExpiredTokens();

    void revokeToken(String token);

    void deleteByToken(String token);

    void deleteRevokedTokensOlderThan(Instant minus);
}

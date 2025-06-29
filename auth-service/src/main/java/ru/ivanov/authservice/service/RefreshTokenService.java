package ru.ivanov.authservice.service;

import ru.ivanov.authservice.model.RefreshToken;
import ru.ivanov.authservice.model.enums.RefreshTokenStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    RefreshTokenStatus getTokenStatus(String token);

    void rotateToken(String oldToken, RefreshToken newToken);

    void revokeAllUserTokens(UUID userId);

    int deleteRevokedTokensOlderThan(Instant cutoffDate);

    int deleteAllExpiredTokens();

    void revokeToken(String token);
}
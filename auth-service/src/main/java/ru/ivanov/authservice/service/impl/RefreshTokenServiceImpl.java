package ru.ivanov.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.authservice.model.RefreshToken;
import ru.ivanov.authservice.model.enums.RefreshTokenStatus;
import ru.ivanov.authservice.repository.RefreshTokenRepository;
import ru.ivanov.authservice.service.RefreshTokenService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenStatus getTokenStatus(String token) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return RefreshTokenStatus.NOT_FOUND;
        }

        RefreshToken refreshToken = tokenOpt.get();

        if (refreshToken.isRevoked()) {
            return RefreshTokenStatus.REVOKED;
        }

        if (refreshToken.isExpired()) {
            return RefreshTokenStatus.EXPIRED;
        }

        return RefreshTokenStatus.ACTIVE;
    }

    @Override
    @Transactional
    public void rotateToken(String oldToken, RefreshToken newToken) {
        refreshTokenRepository.deleteByToken(oldToken);
        refreshTokenRepository.save(newToken);
    }

    @Override
    @Transactional
    public int deleteRevokedTokensOlderThan(Instant cutoff) {
        return refreshTokenRepository.deleteRevokedTokensOlderThan(cutoff);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(userId);
        tokens.forEach(token -> {
            if (!token.isRevoked()) {
                token.revoke();
                refreshTokenRepository.save(token);
            }
        });
    }


    @Override
    @Transactional
    public int deleteAllExpiredTokens() {
        return refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
        });
    }
}
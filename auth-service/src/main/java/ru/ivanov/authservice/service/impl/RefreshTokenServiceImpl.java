package ru.ivanov.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
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

    @Transactional(readOnly = true)
    public boolean existsByToken(String token) {
        return refreshTokenRepository.existsByToken(token);
    }

    @Transactional(readOnly = true)
    public boolean isTokenRevoked(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::isRevoked)
                .orElse(true); // Если токен не найден - считаем отозванным
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

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    public void deleteRevokedTokensOlderThan(Instant minus) {

    }


    @Transactional
    public void rotateToken(String oldToken, RefreshToken newToken) {
        refreshTokenRepository.deleteByToken(oldToken);
        refreshTokenRepository.save(newToken);
    }

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

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void cleanExpiredTokens() {
        Instant now = Instant.now();
        refreshTokenRepository.deleteByExpiryDateBefore(now);
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);
        });
    }
}
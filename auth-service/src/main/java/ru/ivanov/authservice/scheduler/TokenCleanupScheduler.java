package ru.ivanov.authservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ivanov.authservice.service.RefreshTokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void cleanupRevokedTokens() {
        Instant now = Instant.now();
        refreshTokenService.deleteRevokedTokensOlderThan(now.minus(7, ChronoUnit.DAYS));
    }
}

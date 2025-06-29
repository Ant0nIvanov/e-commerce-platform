package ru.ivanov.authservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ivanov.authservice.service.RefreshTokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {
    private static final int REVOKED_RETENTION_DAYS = 7;

    private final RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupRevokedTokens() {
        try {
            log.info("Started revoked tokens cleanup");
            Instant cutoff = Instant.now().minus(REVOKED_RETENTION_DAYS, ChronoUnit.DAYS);
            int deletedTokensCount = refreshTokenService.deleteRevokedTokensOlderThan(cutoff);
            log.info("Deleted {} revoked tokens older than {}", deletedTokensCount, cutoff);
        } catch (Exception e) {
            log.error("Failed to cleanup revoked tokens");
        }
    }

    @Scheduled(cron = "0 30 3 * * *")
    public void cleanupExpiredTokensDaily() {
        try {
            log.info("Started expired tokens cleanup");
            int deletedTokensCount = refreshTokenService.deleteAllExpiredTokens();
            log.info("Deleted {} expired tokens", deletedTokensCount);
        } catch (Exception e) {
            log.error("Failed to cleanup expired tokens");
        }
    }
}
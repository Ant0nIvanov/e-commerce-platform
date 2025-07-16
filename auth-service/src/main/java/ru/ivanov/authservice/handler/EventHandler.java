package ru.ivanov.authservice.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.ivanov.authservice.service.RefreshTokenService;
import ru.ivanov.common.event.UserDeletedEvent;

@Service
@RequiredArgsConstructor
public class EventHandler {
    private final RefreshTokenService refreshTokenService;

    @KafkaListener(topics = {"user-deleted-event-topic"}, groupId = "2")
    public void handleUserDeletedEvent(@Payload UserDeletedEvent event) {
        // хотя при удалении аккаунта можно и удалить
        System.out.println("Получили сообщение");
        refreshTokenService.revokeAllUserTokens(event.userId());
    }
}
package ru.ivanov.cartservice.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.ivanov.cartservice.service.CartItemService;
import ru.ivanov.cartservice.service.CartService;
import ru.ivanov.common.event.UserCreatedEvent;
import ru.ivanov.common.event.UserDeletedEvent;

@Service
@RequiredArgsConstructor
public class EventHandler {
    private final CartService cartService;
    private final CartItemService cartItemService;

    @KafkaListener(topics = {"user-created-event-topic"}, groupId = "1")
    public void handleUserCreatedEvent(@Payload UserCreatedEvent event) {
        cartService.createCart(event.userId());
    }

    @KafkaListener(topics = {"user-deleted-event-topic"}, groupId = "1")
    public void handleUserDeletedEvent(@Payload UserDeletedEvent event) {
        cartItemService.deleteAllItems(event.userId());
        cartService.deleteCartAndAllItems(event.userId());
    }
}
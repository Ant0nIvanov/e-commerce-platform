package ru.ivanov.common.event;

import java.util.UUID;

public record UserCreatedEvent(
    UUID userId
) {
}
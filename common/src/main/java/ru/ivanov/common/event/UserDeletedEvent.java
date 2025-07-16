package ru.ivanov.common.event;

import java.util.UUID;

public record UserDeletedEvent(
        UUID userId
) {
}
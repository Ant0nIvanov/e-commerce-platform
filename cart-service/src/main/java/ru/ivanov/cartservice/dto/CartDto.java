package ru.ivanov.cartservice.dto;

import java.util.UUID;

public record CartDto(
        UUID cartId,
        UUID userId
) {
}
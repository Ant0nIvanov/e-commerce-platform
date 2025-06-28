package ru.ivanov.cartservice.dto;

import java.util.UUID;

public record CartDto(
        UUID id,
        UUID userId
) {
}
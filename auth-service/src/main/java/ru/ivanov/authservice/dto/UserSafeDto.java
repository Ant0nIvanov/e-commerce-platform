package ru.ivanov.authservice.dto;

import java.util.UUID;

public record UserSafeDto(
        UUID id,
        String username,
        String firstName,
        String lastName,
        boolean isAdmin
) {
}
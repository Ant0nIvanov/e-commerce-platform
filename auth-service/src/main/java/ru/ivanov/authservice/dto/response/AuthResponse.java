package ru.ivanov.authservice.dto.response;

import ru.ivanov.authservice.dto.UserSafeDto;

public record AuthResponse(
        UserSafeDto user,
        String accessToken,
        String refreshToken
) {
}
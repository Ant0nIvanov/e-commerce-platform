package ru.ivanov.authservice.dto.response;

public record JwtResponse(
        String accessToken,
        String refreshToken
) {
}
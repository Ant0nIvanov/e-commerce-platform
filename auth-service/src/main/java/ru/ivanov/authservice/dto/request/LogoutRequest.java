package ru.ivanov.authservice.dto.request;

public record LogoutRequest(
        String refreshToken
) {
}

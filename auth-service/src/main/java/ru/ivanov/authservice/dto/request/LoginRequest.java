package ru.ivanov.authservice.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}

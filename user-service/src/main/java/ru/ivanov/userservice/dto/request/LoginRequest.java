package ru.ivanov.userservice.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}

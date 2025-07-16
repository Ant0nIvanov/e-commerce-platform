package ru.ivanov.userservice.dto.request;

public record UpdateUserRequest(
        String username,
        String firstName,
        String lastName,
        String password
) {
}

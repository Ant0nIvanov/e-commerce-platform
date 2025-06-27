package ru.ivanov.userservice.dto.request;

public record UpdateUserRequest(
        String username,
        String password,
        String firstName,
        String lastName
) {
}

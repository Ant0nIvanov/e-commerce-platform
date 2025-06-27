package ru.ivanov.userservice.dto.request;

public record RegistrationRequest(
        String username,
        String firstName,
        String lastName,
        String password
) {
}
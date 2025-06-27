package ru.ivanov.authservice.dto.request;

import lombok.Getter;


public record RegistrationRequest(
        String username,
        String firstName,
        String lastName,
        String password
) {
}

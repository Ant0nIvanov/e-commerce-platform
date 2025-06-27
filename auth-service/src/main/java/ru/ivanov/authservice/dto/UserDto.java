package ru.ivanov.authservice.dto;


import java.util.List;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String firstName,
        String lastName,
        List<String> roles
) {
}
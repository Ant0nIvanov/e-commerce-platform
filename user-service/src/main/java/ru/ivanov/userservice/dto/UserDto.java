package ru.ivanov.userservice.dto;

import java.util.List;
import java.util.UUID;

public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String username,
        List<String> roles
) {
}
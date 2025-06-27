package ru.ivanov.userservice.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String path,
        String message,
        int statusCode,
        LocalDateTime timestamp
) {
}
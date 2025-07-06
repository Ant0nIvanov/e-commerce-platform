package ru.ivanov.authservice.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String path,
        String message,
        int statusCode,
        LocalDateTime timestamp
) {
}
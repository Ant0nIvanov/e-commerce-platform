package ru.ivanov.cartservice.dto;

import java.io.Serializable;
import java.util.UUID;

public record CartDto(
        UUID id,
        UUID userId
) implements Serializable {
}
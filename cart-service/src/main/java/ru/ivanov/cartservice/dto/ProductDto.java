package ru.ivanov.cartservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String description,
        String category,
        BigDecimal price
) {
}

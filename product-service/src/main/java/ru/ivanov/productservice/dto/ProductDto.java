package ru.ivanov.productservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String description,
        String category,
        BigDecimal price
) implements Serializable {
}
package ru.ivanov.cartservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductInCartDto(
        UUID productId,
        String name,
        BigDecimal price,
        int quantity
) {
}
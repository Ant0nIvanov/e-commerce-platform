package ru.ivanov.productservice.dto.request;

import java.math.BigDecimal;

public record UpdateProductRequest(
//        @NotNull(message = "title не должно быть null")
//        @NotBlank(message = "title не должно быть пустым")
        String name,

//        @NotNull(message = "details не должно быть null")
//        @NotBlank(message = "details не должно быть пустым")
        String description,
        String category,
        BigDecimal price
) {
}
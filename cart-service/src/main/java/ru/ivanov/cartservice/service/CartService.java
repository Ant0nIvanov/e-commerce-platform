package ru.ivanov.cartservice.service;


import ru.ivanov.cartservice.dto.CartDto;
import ru.ivanov.cartservice.dto.ProductInCartDto;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartDto createCart(UUID userId);

    List<ProductInCartDto> getCart(UUID userId);

    void addProductToCart(UUID userId, UUID productId);

    void decreaseProductQuantityInCart(UUID userId, UUID productId);

    void removeProductFromCart(UUID userId, UUID productId);

    void deleteCart(UUID userId);
}
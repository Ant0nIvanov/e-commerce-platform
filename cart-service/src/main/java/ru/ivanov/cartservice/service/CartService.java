package ru.ivanov.cartservice.service;

import ru.ivanov.cartservice.dto.ProductInCartDto;
import ru.ivanov.cartservice.model.Cart;

import java.util.List;
import java.util.UUID;

public interface CartService {
    Cart createCart(UUID userId);
    Cart getCart(UUID userId);
    List<ProductInCartDto> getCartItems(UUID userId);
    ProductInCartDto addItemToCart(UUID userId, UUID productId);
    ProductInCartDto decreaseItemQuantityInCart(UUID userId, UUID productId);
    void removeItemFromCart(UUID userId, UUID productId);
    void removeAllItemsFromCart(UUID userId);
    void deleteCartAndAllItems(UUID userId);
}
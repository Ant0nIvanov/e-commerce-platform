package ru.ivanov.cartservice.service;

import ru.ivanov.cartservice.model.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartItemService {
    CartItem save(CartItem item);
    CartItem getCartItem(UUID cartId, UUID productId);
    CartItem getCartItemOrThrow(UUID cartId, UUID productId);
    List<CartItem> getAllItemsByCartId(UUID cartId);
    void deleteByCartIdAndProductId(UUID cartId, UUID productId);
    void delete(CartItem item);
    void deleteAllItems(UUID cartId);
}
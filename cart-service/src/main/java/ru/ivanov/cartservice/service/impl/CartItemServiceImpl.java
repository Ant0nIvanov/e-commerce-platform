package ru.ivanov.cartservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.cartservice.exception.ProductNotFoundInCartException;
import ru.ivanov.cartservice.model.Cart;
import ru.ivanov.cartservice.model.CartItem;
import ru.ivanov.cartservice.repository.CartItemRepository;
import ru.ivanov.cartservice.service.CartItemService;
import ru.ivanov.cartservice.service.CartService;

import java.util.List;
import java.util.UUID;

import static ru.ivanov.cartservice.util.MessageUtils.PRODUCT_NOT_FOUND_IN_CART;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public CartItem save(CartItem item) {
        return cartItemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItem getCartItem(UUID cartId, UUID productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElse(null);
    }

    @Override
    @Transactional
    public CartItem getCartItemOrThrow(UUID cartId, UUID productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ProductNotFoundInCartException(PRODUCT_NOT_FOUND_IN_CART.formatted(productId, cartId)));
    }

    @Override
    @Transactional(readOnly = true)
    // подумать над кешированием отдельных CartItem
    public List<CartItem> getAllItemsByCartId(UUID cartId) {
        return cartItemRepository.findAllByCartId(cartId);
    }

    @Override
    @Transactional
    public void deleteByCartIdAndProductId(UUID cartId, UUID productId) {
        cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
    }

    @Override
    @Transactional
    public void delete(CartItem item) {
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void deleteAllItems(UUID cartId) {
        cartItemRepository.deleteAllByCartId(cartId);
    }
}
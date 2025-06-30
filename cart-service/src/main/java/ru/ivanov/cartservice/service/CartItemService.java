package ru.ivanov.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.cartservice.exception.ProductNotFoundInCartException;
import ru.ivanov.cartservice.model.CartItem;
import ru.ivanov.cartservice.repository.CartItemRepository;

import java.util.List;
import java.util.UUID;

import static ru.ivanov.cartservice.util.MessageUtils.PRODUCT_NOT_FOUND_IN_CART;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    @Transactional
    public void save(CartItem item) {
        cartItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public CartItem getCartItem(UUID cartId, UUID productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElse(null);
    }

    public CartItem getCartItemOrThrow(UUID cartId, UUID productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ProductNotFoundInCartException(PRODUCT_NOT_FOUND_IN_CART.formatted(productId, cartId)));
    }


    @Transactional(readOnly = true)
    public List<CartItem> getAllItemsByCartId(UUID cartId) {
        return cartItemRepository.findAllByCartId(cartId);
    }


    @Transactional
    public void deleteByCartIdAndProductId(UUID cartId, UUID productId) {
        cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
    }

    @Transactional
    public void delete(CartItem item) {
        cartItemRepository.delete(item);
    }
}
package ru.ivanov.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.cartservice.exception.CartNotFoundException;
import ru.ivanov.cartservice.model.Cart;
import ru.ivanov.cartservice.repository.CartRepository;

import java.util.UUID;

import static ru.ivanov.cartservice.util.MessageUtils.CART_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CartEntityService {

    private final CartRepository cartRepository;

    @Cacheable(value = "carts", key = "#userId")
    @Transactional
    public Cart getCart(UUID userId) {
        return cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND.formatted(userId)));
    }
}

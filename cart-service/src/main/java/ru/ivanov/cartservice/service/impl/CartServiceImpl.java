package ru.ivanov.cartservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.cartservice.client.ProductClient;
import ru.ivanov.cartservice.dto.CartDto;
import ru.ivanov.cartservice.dto.ProductDto;
import ru.ivanov.cartservice.dto.ProductInCartDto;
import ru.ivanov.cartservice.exception.CartNotFoundException;
import ru.ivanov.cartservice.exception.ProductNotFoundException;
import ru.ivanov.cartservice.exception.ProductNotFoundInCartException;
import ru.ivanov.cartservice.mapper.CartMapper;
import ru.ivanov.cartservice.model.Cart;
import ru.ivanov.cartservice.model.CartItem;
import ru.ivanov.cartservice.repository.CartRepository;
import ru.ivanov.cartservice.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.ivanov.cartservice.util.MessageUtils.CART_NOT_FOUND_WITH_USER_ID;
import static ru.ivanov.cartservice.util.MessageUtils.PRODUCT_NOT_FOUND_IN_CART;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    public final CartRepository cartRepository;
    public final ProductClient productClient;
    public final CartMapper cartMapper;

    @Override
    @Transactional
    public CartDto createCart(UUID userId) {
        Cart cart = new Cart(userId);
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInCartDto> getCart(UUID userId) {
        Cart cart = findCartByUserId(userId);

        List<CartItem> cartItems = cart.getItems();

        Map<UUID, Integer> productQuantities = cartItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        CartItem::getQuantity
                ));

        List<UUID> productsIDs = productQuantities.keySet().stream().toList();

        List<ProductDto> products = productClient.getProducts(productsIDs);

        return products.stream()
                .map(product -> new ProductInCartDto(
                        product.id(),
                        product.name(),
                        product.price(),
                        productQuantities.get(product.id())
                )).toList();
    }

    @Override
    @Transactional
    public void addProductToCart(UUID userId, UUID productId) {
        if (!productClient.isProductExists(productId)) {
            throw new ProductNotFoundException("product not found");
        }

        Cart cart = findCartByUserId(userId);

        CartItem item = findItemInCartOrCreateNew(cart, productId);

        item.incrementQuantity();

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void decreaseProductQuantityInCart(UUID userId, UUID productId) {
        if (!productClient.isProductExists(productId)) {
            throw new ProductNotFoundException("product not found");
        }

        Cart cart = findCartByUserId(userId);

        CartItem item = findItemInCartOrThrow(cart, productId);

        item.decreaseQuantity();

        if (item.getQuantity() == 0) {
            cart.removeItem(item);
        }

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeProductFromCart(UUID userId, UUID productId) {
        if (!productClient.isProductExists(productId)) {
            throw new ProductNotFoundException("product not found");
        }

        Cart cart = findCartByUserId(userId);

        CartItem item = findItemInCartOrThrow(cart, productId);

        cart.removeItem(item);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteCart(UUID userId) {
        if (!cartRepository.existsByUserId(userId)) {
            throw new CartNotFoundException(CART_NOT_FOUND_WITH_USER_ID.formatted(userId));
        }
        cartRepository.deleteByUserId(userId);
    }

    private Cart findCartByUserId(UUID userId) {
        return cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND_WITH_USER_ID.formatted(userId)));
    }

    private CartItem findItemInCartOrCreateNew(Cart cart, UUID productId) {
        return cart.getItems().stream()
                .filter(item -> Objects.equals(item.getProductId(), productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem(cart, productId);
                    cart.getItems().add(newItem);
                    return newItem;
                });
    }

    private CartItem findItemInCartOrThrow(Cart cart, UUID productId) {
        List<CartItem> items = cart.getItems();
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundInCartException(PRODUCT_NOT_FOUND_IN_CART
                        .formatted(productId, cart.getId())
                ));
    }
}
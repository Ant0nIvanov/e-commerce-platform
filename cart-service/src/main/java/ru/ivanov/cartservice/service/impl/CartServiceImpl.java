package ru.ivanov.cartservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.cartservice.client.ProductClient;
import ru.ivanov.cartservice.dto.CartDto;
import ru.ivanov.cartservice.dto.ProductDto;
import ru.ivanov.cartservice.dto.ProductInCartDto;
import ru.ivanov.cartservice.exception.CartNotFoundException;
import ru.ivanov.cartservice.exception.ProductNotFoundException;
import ru.ivanov.cartservice.mapper.CartMapper;
import ru.ivanov.cartservice.model.Cart;
import ru.ivanov.cartservice.model.CartItem;
import ru.ivanov.cartservice.repository.CartItemRepository;
import ru.ivanov.cartservice.repository.CartRepository;
import ru.ivanov.cartservice.service.CartEntityService;
import ru.ivanov.cartservice.service.CartItemService;
import ru.ivanov.cartservice.service.CartService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.ivanov.cartservice.util.MessageUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartEntityService cartEntityService;
    public final CartRepository cartRepository;
    public final CartItemRepository cartItemRepository;
    public final CartItemService cartItemService;
    public final ProductClient productClient;
    public final CartMapper cartMapper;
    public final CacheManager cacheManager;

    @Override
    @CachePut(value = "cartDtos", key = "#userId")
    @Transactional
    public CartDto createCart(UUID userId) {
        Cart cart = new Cart(userId);
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInCartDto> getCartWithItems(UUID userId) {
        Cart cart = cartEntityService.getCart(userId);

        List<CartItem> cartItems = cartItemService.getAllItemsByCartId(cart.getId());

        Map<UUID, Integer> productQuantities = cartItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        CartItem::getQuantity
                ));

        List<UUID> productsIDs = productQuantities.keySet().stream().toList();

        if (productsIDs.isEmpty()) {
            return Collections.emptyList();
        }

        Cache cache = cacheManager.getCache("products");

        if (cache == null) {
           return productClient.getProducts(productsIDs).stream()
                   .map(product -> new ProductInCartDto(
                           product.id(),
                           product.name(),
                           product.price(),
                           productQuantities.get(product.id())
                   )).toList();
        }

        List<ProductInCartDto> productsInCart = new ArrayList<>();
        List<UUID> missingIds = new ArrayList<>();

        for (UUID id : productsIDs) {
            ProductDto cachedProduct = cache.get(id, ProductDto.class);
            if (cachedProduct != null) {
                productsInCart.add(new ProductInCartDto(
                        cachedProduct.id(),
                        cachedProduct.name(),
                        cachedProduct.price(),
                        productQuantities.get(cachedProduct.id())
                ));
            } else {
                missingIds.add(id);
            }
        }

        if (!missingIds.isEmpty()) {
            List<ProductDto> missingProducts = productClient.getProducts(missingIds);
            for (ProductDto product : missingProducts) {
                productsInCart.add(new ProductInCartDto(
                        product.id(),
                        product.name(),
                        product.price(),
                        productQuantities.get(product.id())
                ));

                cache.put(product.id(), product);
            }
        }

        return productsInCart;
    }

    @Override
    @Transactional
    public void addProductToCart(UUID userId, UUID productId) {
        if (!productClient.isProductExists(productId)) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.formatted(productId));
        }

        Cart cart = cartEntityService.getCart(userId);

        CartItem item = cartItemService.getCartItem(cart.getId(), productId);

        if (item == null) {
            item = new CartItem(cart.getId(), productId);
        }

        item.incrementQuantity();
        cartItemService.save(item);
    }

    @Override
    @Transactional
    public void decreaseProductQuantityInCart(UUID userId, UUID productId) {
        if (!productClient.isProductExists(productId)) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.formatted(productId));
        }

        Cart cart = cartEntityService.getCart(userId);

        CartItem item = cartItemService.getCartItemOrThrow(cart.getId(), productId);

        item.decreaseQuantity();

        if (item.getQuantity() == 0) {
            cartItemService.delete(item);
            return;
        }

        cartItemService.save(item);
    }

    @Override
    @Transactional
    public void removeProductFromCart(UUID userId, UUID productId) {
        if (!productClient.isProductExists(productId)) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.formatted(productId));
        }

        Cart cart = cartEntityService.getCart(userId);

        CartItem item = cartItemService.getCartItemOrThrow(cart.getId(), productId);

        cartItemService.delete(item);
    }

    @Override
    @CacheEvict(value = "carts", key = "#userId")
    @Transactional
    public void deleteCart(UUID userId) {
        if (!cartRepository.existsByUserId(userId)) {
            throw new CartNotFoundException(CART_NOT_FOUND.formatted(userId));
        }
        cartRepository.deleteByUserId(userId);
    }
}
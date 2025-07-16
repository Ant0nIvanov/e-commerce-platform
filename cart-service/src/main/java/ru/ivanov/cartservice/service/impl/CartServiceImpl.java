package ru.ivanov.cartservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.cartservice.client.ProductClient;
import ru.ivanov.cartservice.dto.ProductDto;
import ru.ivanov.cartservice.dto.ProductInCartDto;
import ru.ivanov.cartservice.exception.CartNotFoundException;
import ru.ivanov.cartservice.exception.ProductNotFoundException;
import ru.ivanov.cartservice.model.Cart;
import ru.ivanov.cartservice.model.CartItem;
import ru.ivanov.cartservice.repository.CartRepository;
import ru.ivanov.cartservice.service.CartItemService;
import ru.ivanov.cartservice.service.CartService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.ivanov.cartservice.util.MessageUtils.CART_NOT_FOUND;
import static ru.ivanov.cartservice.util.MessageUtils.PRODUCT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    @Lazy
    @Autowired
    private CartService self;
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final ProductClient productClient;
    private final CacheManager cacheManager;

    private static final String CART_CACHE_NAME = "cart-service:carts";
    private static final String PRODUCT_CACHE_NAME = "cart-service:products";

    @Override
    @CachePut(value = CART_CACHE_NAME, key = "#userId")
    @Transactional
    public Cart createCart(UUID userId) {
        Cart cart = new Cart(userId);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    @Cacheable(value = CART_CACHE_NAME, key = "#userId")
    @Transactional(readOnly = true)
    public Cart getCart(UUID userId) {
        return cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND.formatted(userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInCartDto> getCartItems(UUID userId) {
        Cart cart = self.getCart(userId);

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

        Cache cache = cacheManager.getCache(PRODUCT_CACHE_NAME);

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
    public ProductInCartDto addItemToCart(UUID userId, UUID productId) {
//        if (!productClient.isProductExists(productId)) {
//            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.formatted(productId));
//        }

        Cart cart = self.getCart(userId);

        CartItem item = cartItemService.getCartItem(cart.getId(), productId);

        ProductDto product = productClient.getProduct(productId);

        if (item == null) {
            item = new CartItem(cart.getId(), productId);
        }

        item.incrementQuantity();
        cartItemService.save(item);
        return new ProductInCartDto(item.getProductId(), product.name(), product.price(), item.getQuantity());
    }

    @Override
    @Transactional
    public ProductInCartDto decreaseItemQuantityInCart(UUID userId, UUID productId) {
//        if (!productClient.isProductExists(productId)) {
//            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.formatted(productId));
//        }

        Cart cart = self.getCart(userId);

        CartItem item = cartItemService.getCartItemOrThrow(cart.getId(), productId);

        ProductDto product = productClient.getProduct(productId);

        item.decreaseQuantity();

        if (item.getQuantity() == 0) {
            cartItemService.delete(item);
            return new ProductInCartDto(item.getProductId(), product.name(), product.price(), item.getQuantity());
        }

        cartItemService.save(item);
        return new ProductInCartDto(item.getProductId(), product.name(), product.price(), item.getQuantity());
    }

    @Override
    @Transactional
    public void removeItemFromCart(UUID userId, UUID productId) {
//        if (!productClient.isProductExists(productId)) {
//            throw new ProductNotFoundException(PRODUCT_NOT_FOUND.formatted(productId));
//        }

        Cart cart = self.getCart(userId);

        CartItem item = cartItemService.getCartItemOrThrow(cart.getId(), productId);

        cartItemService.delete(item);
    }

    @Override
    @Transactional
    public void removeAllItemsFromCart(UUID userId) {
        Cart cart = self.getCart(userId);
        cartItemService.deleteAllItems(cart.getId());
    }

    @Override
    @CacheEvict(value = CART_CACHE_NAME, key = "#userId")
    @Transactional
    public void deleteCartAndAllItems(UUID userId) {
        Cart cart = self.getCart(userId);
        cartItemService.deleteAllItems(cart.getId());
        cartRepository.delete(cart);
    }
}
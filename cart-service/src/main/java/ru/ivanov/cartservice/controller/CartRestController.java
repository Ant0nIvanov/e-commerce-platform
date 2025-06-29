package ru.ivanov.cartservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.cartservice.dto.ProductInCartDto;
import ru.ivanov.cartservice.security.UserDetailsImpl;
import ru.ivanov.cartservice.service.CartService;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/carts")
public class CartRestController {

    private final CartService cartService;

//    @PostMapping("/new")
//    // todo изучить кафку и создать подписку на событие UserRegisteredEvent
//    public ResponseEntity<CartDto> createCartForNewUser(@RequestParam("userId") UUID userId) {
//        CartDto cart = cartService.createCart(userId);
//        return ResponseEntity.created(null)
//                .contentType(APPLICATION_JSON)
//                .body(cart);
//    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductInCartDto>> getCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID userId = userDetails.getUserId();
        List<ProductInCartDto> products = cartService.getCartWithItems(userId);
        System.out.println(products);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/items/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> addProductToCart(
            @PathVariable("productId") UUID productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID userId = userDetails.getUserId();
        cartService.addProductToCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("items/{productId}/decrement")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> decrementProductQuantityInCart(
            @PathVariable("productId") UUID productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID userId = userDetails.getUserId();
        cartService.decreaseProductQuantityInCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("items/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeProductFromCart(
            @PathVariable("productId") UUID productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID userId = userDetails.getUserId();
        cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String admin() {
//        return "admin";
//    }

//    @DeleteMapping
    // todo изучить кафку и создать подписку на событие UserDeletedEvent
//    public ResponseEntity<Void> removeCart(
//            @RequestHeader("Authorization") String token,
//            @RequestParam("userId") UUID userId
//    ) {
//        cartService.deleteCart(userId);
//        return ResponseEntity.noContent().build();
//    }
}
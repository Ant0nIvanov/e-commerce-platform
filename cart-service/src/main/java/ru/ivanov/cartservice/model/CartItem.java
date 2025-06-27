package ru.ivanov.cartservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "carts_products")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
//    private UUID cartId;

    private UUID productId;

    private int quantity = 0;

    public CartItem(Cart cart, UUID productId) {
        this.cart = cart;
        this.productId = productId;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        quantity--;
    }
}
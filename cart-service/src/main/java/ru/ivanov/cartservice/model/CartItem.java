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

    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "product_id")
    private UUID productId;

    private int quantity = 0;

    public CartItem(UUID cartId, UUID productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        quantity--;
    }
}
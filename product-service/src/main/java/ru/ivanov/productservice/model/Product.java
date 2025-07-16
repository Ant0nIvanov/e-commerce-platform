package ru.ivanov.productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ivanov.productservice.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private BigDecimal price;

    private int quantity;

    private UUID sellerId;

    // todo временно оставляю этот конструктор
    public Product(String name, String description, ProductCategory category, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    public Product(String name, String description, ProductCategory category, BigDecimal price, int quantity, UUID sellerId) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.sellerId = sellerId;
    }
}
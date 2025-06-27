package ru.ivanov.productservice.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.ivanov.productservice.model.Product;
import ru.ivanov.productservice.model.Product_;
import ru.ivanov.productservice.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.Optional;

public class ProductSpecification {
    public static Specification<Product> filterByCategory(ProductCategory productCategory) {
        return (root, query, criteriaBuilder) ->
                Optional.ofNullable(productCategory)
                        .map(category -> criteriaBuilder.equal(root.get(Product_.CATEGORY), category))
                        .orElseGet(criteriaBuilder::conjunction);
    }

    public static Specification<Product> filterByMinPrice(BigDecimal minPrice) {
        return ((root, query, criteriaBuilder) ->
                Optional.ofNullable(minPrice)
                        .map(price -> criteriaBuilder.greaterThanOrEqualTo(root.get(Product_.PRICE), minPrice))
                        .orElseGet(criteriaBuilder::conjunction)
                );
    }

    public static Specification<Product> filterByMaxPrice(BigDecimal maxPrice) {
        return ((root, query, criteriaBuilder) ->
                Optional.ofNullable(maxPrice)
                        .map(price -> criteriaBuilder.lessThanOrEqualTo(root.get(Product_.PRICE), maxPrice))
                        .orElseGet(criteriaBuilder::conjunction)
        );
    }
}
package ru.ivanov.productservice.service;

import org.springframework.data.domain.Pageable;
import ru.ivanov.productservice.dto.ProductDto;
import ru.ivanov.productservice.dto.request.CreateProductRequest;
import ru.ivanov.productservice.dto.request.UpdateProductRequest;
import ru.ivanov.productservice.dto.response.PagedResponse;
import ru.ivanov.productservice.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {

    ProductDto createProduct(CreateProductRequest request);

    PagedResponse<ProductDto> getAllProducts(Pageable pageable, ProductCategory category, BigDecimal minPrice, BigDecimal maxPrice);

    List<ProductDto> getProductsByID(List<UUID> productsIDs);

    ProductDto getProductById(UUID productId);

    ProductDto updateProduct(UUID productId, UpdateProductRequest request);

    void deleteProduct(UUID productId);

    boolean productExists(UUID productId);

    Map<String, String> getProductsCategories();
}
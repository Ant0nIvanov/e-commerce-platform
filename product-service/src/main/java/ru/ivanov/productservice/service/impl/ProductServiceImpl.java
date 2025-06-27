package ru.ivanov.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.productservice.dto.ProductDto;
import ru.ivanov.productservice.dto.request.CreateProductRequest;
import ru.ivanov.productservice.dto.request.UpdateProductRequest;
import ru.ivanov.productservice.dto.response.PagedResponse;
import ru.ivanov.productservice.exception.ProductNotFoundException;
import ru.ivanov.productservice.mapper.ProductMapper;
import ru.ivanov.productservice.model.Product;
import ru.ivanov.productservice.model.enums.ProductCategory;
import ru.ivanov.productservice.repository.ProductRepository;
import ru.ivanov.productservice.repository.specification.ProductSpecification;
import ru.ivanov.productservice.service.ProductService;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.ivanov.productservice.util.MessageUtils.PRODUCT_NOT_FOUND_WITH_ID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;

    @Override
    @CachePut(value = "products", key = "#result.id")
    @CacheEvict(value = "productQueries", allEntries = true)
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByID(List<UUID> productsIDs) {
        Map<UUID, ProductDto> cache = Objects.requireNonNull(cacheManager.getCache("products"))
                .get(productsIDs, () -> loadProductsBatch(productsIDs));

        return productsIDs.stream()
                .map(id -> Objects.requireNonNull(cache).getOrDefault(id, null))
                .filter(Objects::nonNull)
                .toList();
    }

    private Map<UUID, ProductDto> loadProductsBatch(List<UUID> ids) {
        return productRepository.findAllById(ids).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toMap(
                        ProductDto::id,
                        Function.identity()
                ));
    }

    @Override
    @Cacheable(value = "productsQueries", key = "{#pageable.pageNumber, #pageable.pageSize, #category?.name(), #minPrice, #maxPrice}")
    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getAllProducts(Pageable pageable, ProductCategory category, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Product> spec = Specification.allOf(
                ProductSpecification.filterByCategory(category),
                ProductSpecification.filterByMinPrice(minPrice),
                ProductSpecification.filterByMaxPrice(maxPrice)
        );
        Page<Product> page = productRepository.findAll(spec, pageable);
        return PagedResponse.fromPage(page.map(productMapper::toDto));
    }

    @Override
    @Cacheable(value = "products", key = "#productId")
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        Product product = findById(productId);
        return productMapper.toDto(product);
    }

    @Override
    @CachePut(value = "products", key = "#productId")
    @CacheEvict(value = "productQueries", allEntries = true)
    @Transactional
    public ProductDto updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = findById(productId);

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(ProductCategory.fromName(request.category()));
        product.setPrice(request.price());

        productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    @CacheEvict(value = {"products", "productExistence"}, key = "#productId")
    @Transactional
    public void deleteProduct(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_WITH_ID.formatted(productId));
        }
        productRepository.deleteById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productExistence", key = "#productId", unless = "#result == false")
    public boolean productExists(UUID productId) {
        return productRepository.existsById(productId);
    }

    private Product findById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_WITH_ID.formatted(productId)));
    }

    @Override
    public Map<String, String> getProductsCategories() {
        return Arrays.stream(ProductCategory.values())
                .collect(Collectors.toMap(
                        ProductCategory::name,
                        category -> category.russianName
                ));
    }
}
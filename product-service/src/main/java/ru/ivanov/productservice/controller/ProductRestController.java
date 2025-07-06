package ru.ivanov.productservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ivanov.productservice.dto.ProductDto;
import ru.ivanov.productservice.dto.request.CreateProductRequest;
import ru.ivanov.productservice.dto.request.UpdateProductRequest;
import ru.ivanov.productservice.dto.response.PagedResponse;
import ru.ivanov.productservice.model.enums.ProductCategory;
import ru.ivanov.productservice.service.ProductService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("api/v1/products")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto product = productService.createProduct(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{productId}")
                .buildAndExpand(product.id())
                .toUri();
        return ResponseEntity.created(location)
                .contentType(APPLICATION_JSON)
                .body(product);
    }

    @GetMapping("/{productId}/exists")
//    @PreAuthorize("hasRole('CART-SERVICE')")
    public ResponseEntity<Void> checkProductExists(@PathVariable UUID productId) {
        boolean exists = productService.productExists(productId);
        return exists
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-ids")
//    @PreAuthorize("hasRole('CART-SERVICE')")
    public ResponseEntity<List<ProductDto>> getProductsById(
           @RequestParam(name = "ids") List<UUID> productsIDs
    ) {
        List<ProductDto> products = productService.getProductsByID(productsIDs);
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(products);
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, String>> getProductCategories() {
        Map<String, String> categories = productService.getProductsCategories();
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(categories);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductDto>> getAllProducts(
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", required = false, defaultValue = "10")  int pageSize,
            @RequestParam(name = "category", required = false) ProductCategory category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice
    ) {
        PagedResponse<ProductDto> page = productService.getAllProducts(
                PageRequest.of(pageNumber, pageSize),
                category,
                minPrice,
                maxPrice
        );
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(page);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("productId") UUID productId) {
        ProductDto product = productService.getProductById(productId);
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(product);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable("productId") UUID productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductDto updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
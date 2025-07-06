package ru.ivanov.cartservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ivanov.cartservice.dto.ProductDto;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "PRODUCT-SERVICE")
public interface CartInterface {

    @GetMapping("/api/v1/products/{productId}/exists")
    ResponseEntity<Void> checkProductExists(@PathVariable UUID productId);

    @GetMapping("/api/v1/products/by-ids")
    ResponseEntity<List<ProductDto>> getProductsById(@RequestParam(name = "ids") List<UUID> productsIDs);
}
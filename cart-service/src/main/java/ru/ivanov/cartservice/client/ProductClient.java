package ru.ivanov.cartservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.ivanov.cartservice.dto.ProductDto;
import ru.ivanov.cartservice.exception.ExternalServiceUnavailableException;
import ru.ivanov.cartservice.exception.ProductServiceException;
import ru.ivanov.cartservice.feign.CartInterface;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.ivanov.cartservice.util.MessageUtils.PRODUCT_SERVICE_IS_CURRENTLY_UNAVAILABLE;

@Service
@RequiredArgsConstructor
public class ProductClient {
    private static final String PRODUCT_CACHE_NAME = "cart-service:products";
    private final CartInterface cartInterface;

    public List<ProductDto> getProducts(List<UUID> productsIDs) {
        ResponseEntity<List<ProductDto>> response = cartInterface.getProductsById(productsIDs);
        return response.getBody();
    }

    public boolean isProductExists(UUID productId) {
        ResponseEntity<Void> response =  cartInterface.checkProductExists(productId);

        if (response.getStatusCode() == HttpStatus.OK) {
            return true;
        }

        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return false;
        }
        throw new RuntimeException();
    }

    @Cacheable(value = PRODUCT_CACHE_NAME, key = "#productId")
    public ProductDto getProduct(UUID productId) {
        ResponseEntity<ProductDto> response = cartInterface.getProductById(productId);
        return response.getBody();
    }
}
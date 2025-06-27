package ru.ivanov.cartservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.List;
import java.util.UUID;

import static ru.ivanov.cartservice.util.MessageUtils.PRODUCT_SERVICE_IS_CURRENTLY_UNAVAILABLE;

@Service
@RequiredArgsConstructor
public class ProductClient {

    @Value("${product.service.url}")
    public String BASE_URL;

    public final RestTemplate restTemplate;

    public List<ProductDto> getProducts(List<UUID> productsIDs) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(BASE_URL + "/by-ids")
                .queryParam("ids", productsIDs);

        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProductDto>>() {}
        );

        return response.getBody();
    }

    public boolean isProductExists(UUID productId) {
        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .path("/{productId}/exists")
                .buildAndExpand(productId)
                .toUriString();

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Boolean.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return Boolean.TRUE.equals(response.getBody());
            }

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }

            throw new ProductServiceException("Invalid response from Product Service: "
                                              + response.getStatusCode() + ", body: " + response.getBody());

        }  catch (RestClientException ex) {
            throw new ExternalServiceUnavailableException(PRODUCT_SERVICE_IS_CURRENTLY_UNAVAILABLE);
        }
    }
}
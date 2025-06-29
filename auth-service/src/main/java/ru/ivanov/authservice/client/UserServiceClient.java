package ru.ivanov.authservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;
import ru.ivanov.authservice.exception.UsernameIsTakenException;
import ru.ivanov.authservice.util.JWTUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final JWTUtils jwtUtils;

    @Value("${user.service.url}")
    private String BASE_URL;

    @CachePut(value = "users", key = "#result.id")
    public UserDto createNewUser(RegistrationRequest request) {
        String serviceToken = jwtUtils.generateServiceToken(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<RegistrationRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.POST,
                    entity,
                    UserDto.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getBody();
            } else {
                throw new RuntimeException("Unexpected status: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                String errorResponse = e.getResponseBodyAsString();
                throw new UsernameIsTakenException(errorResponse);
            } else {
                throw new RuntimeException("User service error: " + e.getStatusCode(), e);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Network error: " + e.getMessage(), e);
        }
    }

    @CachePut(value = "users", key = "#result.id")
    public UserDto verifyCredentials(LoginRequest request) {
        String serviceToken = jwtUtils.generateServiceToken(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    BASE_URL + "/verify-credentials",
                    HttpMethod.POST,
                    entity,
                    UserDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to verify credentials. Status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error calling user service: " + e.getMessage(), e);
        }
    }

    @Cacheable(value = "users", key = "#userId")
    public UserDto getUserById(UUID userId) {
        String serviceToken = jwtUtils.generateServiceToken(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    entity,
                    UserDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException(response.toString());
            }
        } catch (RestClientException ex) {
            throw new RuntimeException(ex);
        }
    }
}
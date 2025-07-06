package ru.ivanov.authservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;
import ru.ivanov.authservice.exception.UsernameIsTakenException;
import ru.ivanov.authservice.feign.AuthInterface;
import ru.ivanov.authservice.util.JWTUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserClient {

    private final JWTUtils jwtUtils;
    private final AuthInterface authInterface;

    @CachePut(value = "users", key = "#result.id")
    public UserDto createNewUser(RegistrationRequest request) {
        String serviceToken = jwtUtils.generateServiceToken(null);

        try {
            String authHeader = "Bearer " + serviceToken;
            ResponseEntity<UserDto> response = authInterface.createUser(authHeader, request);


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

        try {
            String authHeader = "Bearer " + serviceToken;
            ResponseEntity<UserDto> response = authInterface.verifyCredentials(authHeader, request);

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

        try {
            String authHeader = "Bearer " + serviceToken;
            ResponseEntity<UserDto> response = authInterface.getUser(authHeader);

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
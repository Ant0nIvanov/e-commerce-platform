package ru.ivanov.authservice.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;
import ru.ivanov.authservice.security.UserDetailsImpl;

import java.util.UUID;

@FeignClient(value = "USER-SERVICE")
public interface AuthInterface {

    @PostMapping("/api/v1/users")
    ResponseEntity<UserDto> createUser(@RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RegistrationRequest request);

    @PostMapping("/api/v1/users/verify-credentials")
    ResponseEntity<UserDto> verifyCredentials(@RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody LoginRequest request);

    @GetMapping
    ResponseEntity<UserDto> getUser(@RequestHeader("Authorization") String authHeader);
}
package ru.ivanov.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RefreshTokenRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;
import ru.ivanov.authservice.dto.response.AuthResponse;
import ru.ivanov.authservice.dto.response.JwtResponse;
import ru.ivanov.authservice.service.AuthService;

import java.net.URI;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.created(URI.create("/users/" + response.user().id()))
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        JwtResponse response = authService.refreshToken(request);
        return ResponseEntity.ok()
                .contentType(APPLICATION_JSON)
                .body(response);
    }
}
package ru.ivanov.authservice.service;


import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RefreshTokenRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;
import ru.ivanov.authservice.dto.response.AuthResponse;
import ru.ivanov.authservice.dto.response.JwtResponse;

public interface AuthService {
    AuthResponse register(RegistrationRequest request);

    AuthResponse login(LoginRequest request);

    JwtResponse refresh(RefreshTokenRequest request);

    void logout(String token);
}
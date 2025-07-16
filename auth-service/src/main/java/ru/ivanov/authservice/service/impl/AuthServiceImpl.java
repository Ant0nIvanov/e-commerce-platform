package ru.ivanov.authservice.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.authservice.client.UserClient;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.dto.UserSafeDto;
import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RefreshTokenRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;
import ru.ivanov.authservice.dto.response.AuthResponse;
import ru.ivanov.authservice.dto.response.JwtResponse;
import ru.ivanov.authservice.exception.AuthException;
import ru.ivanov.authservice.mapper.UserDtoMapper;
import ru.ivanov.authservice.model.RefreshToken;
import ru.ivanov.authservice.model.enums.RefreshTokenStatus;
import ru.ivanov.authservice.service.AuthService;
import ru.ivanov.authservice.service.RefreshTokenService;
import ru.ivanov.authservice.util.JWTUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UserClient userClient;
    private final JWTUtils jwtUtils;
    private final UserDtoMapper userDtoMapper;

    @Override
    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        UserDto createdUser =  userClient.createNewUser(request);

        String accessToken = jwtUtils.generateAccessToken(createdUser);
        RefreshToken refreshToken = jwtUtils.generateRefreshToken(createdUser);

        refreshTokenService.save(refreshToken);

        UserSafeDto userSafeDto = userDtoMapper.toSafeDto(createdUser);

        return new AuthResponse(userSafeDto, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserDto user = userClient.verifyCredentials(request);

        String accessToken = jwtUtils.generateAccessToken(user);
        RefreshToken refreshToken = jwtUtils.generateRefreshToken(user);

        refreshTokenService.revokeAllUserTokens(user.id());
        refreshTokenService.save(refreshToken);

        UserSafeDto userSafeDto = userDtoMapper.toSafeDto(user);
        return new AuthResponse(userSafeDto, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public JwtResponse refresh(RefreshTokenRequest request) {
        String oldRefreshToken = request.refreshToken();

        RefreshTokenStatus status = refreshTokenService.getTokenStatus(oldRefreshToken);

        if (status != RefreshTokenStatus.ACTIVE) {
            throw new AuthException("Недействительный токен. Статус токена: " + status.name());
        }

        UUID userId = jwtUtils.validateRefreshTokenAndExtractUserId(oldRefreshToken);

        UserDto userDto = userClient.getUserById(userId);

        String newAccessToken = jwtUtils.generateAccessToken(userDto);
        RefreshToken newRefreshToken = jwtUtils.generateRefreshToken(userDto);

        refreshTokenService.rotateToken(oldRefreshToken, newRefreshToken);

        return new JwtResponse(newAccessToken, newRefreshToken.getToken());
    }

    @Override
    @Transactional
    public void logout(String token) {
        refreshTokenService.findByToken(token)
                .ifPresent(refreshToken -> {
                    if (refreshToken.isRevoked()) {
                        return;
                    }

                    refreshTokenService.revokeToken(refreshToken.getToken());
                });
    }
}
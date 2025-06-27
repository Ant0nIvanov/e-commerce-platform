package ru.ivanov.authservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivanov.authservice.client.UserServiceClient;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.dto.UserSafeDto;
import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RefreshTokenRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;
import ru.ivanov.authservice.dto.response.AuthResponse;
import ru.ivanov.authservice.dto.response.JwtResponse;
import ru.ivanov.authservice.mapper.UserDtoMapper;
import ru.ivanov.authservice.service.AuthService;
import ru.ivanov.authservice.util.JWTUtils;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userClient;
    private final JWTUtils jwtUtils;
    private final UserDtoMapper userDtoMapper;

    @Override
    public AuthResponse register(RegistrationRequest request) {
        UserDto createdUserDto =  userClient.createNewUser(request);

        String accessToken = jwtUtils.generateAccessToken(createdUserDto);
        String refreshToken = jwtUtils.generateRefreshToken(createdUserDto);

        UserSafeDto userSafeDto = userDtoMapper.toSafeDto(createdUserDto);

        return new AuthResponse(userSafeDto, accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String credentials = request.username() + ":" + request.password();
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());


        UserDto userDto = userClient.verifyCredentials(request);

        String accessToken = jwtUtils.generateAccessToken(userDto);
        String refreshToken = jwtUtils.generateRefreshToken(userDto);

        UserSafeDto userSafeDto = userDtoMapper.toSafeDto(userDto);
        return new AuthResponse(userSafeDto, accessToken, refreshToken);
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String oldRefreshToken = request.refreshToken();
        //валидация
        //    if (!tokenProvider.validateTokenStructure(refreshToken)) {
//        throw new InvalidTokenException("Invalid token structure");
//    }


        // Проверка существования refresh токена
//        if (!refreshTokenService.existsByToken(oldRefreshToken)) {
//            throw new AuthException("Переданный refresh-токен не действителен");
//        }

        //    // 2. Проверка отзыва токена
//        if (refreshTokenService.isTokenRevoked(refreshToken)) {
//        throw new TokenRevokedException("Token was revoked");
//    }

        UUID userId = jwtUtils.extractUserIdFromRefreshToken(oldRefreshToken);

        UserDto userDto = userClient.getUserById(userId);

        // Генерация новых токенов
        String accessToken = jwtUtils.generateAccessToken(userDto);
        String refreshToken = jwtUtils.generateRefreshToken(userDto);

        // Удаление старого refresh токена и сохранение нового
//        refreshTokenService.deleteByToken(oldRefreshToken);
//        refreshTokenService.save(new RefreshToken(refreshToken, userDetails.getId()));

        // Возврат новых токенов
        return new JwtResponse(accessToken, refreshToken);
    }
}
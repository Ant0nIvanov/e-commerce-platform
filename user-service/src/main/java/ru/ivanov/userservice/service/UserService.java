package ru.ivanov.userservice.service;

import jakarta.validation.Valid;
import ru.ivanov.userservice.dto.UserDto;
import ru.ivanov.userservice.dto.request.RegistrationRequest;
import ru.ivanov.userservice.dto.request.UpdateUserRequest;

import java.util.UUID;

public interface UserService {
    UserDto createUser(RegistrationRequest request);

    UserDto verifyCredentials(String username, String password);

    UserDto getUser(UUID userId);

    UserDto updateUserPatch(UUID userId, UpdateUserRequest request);

    void deleteUserById(UUID userId);
}
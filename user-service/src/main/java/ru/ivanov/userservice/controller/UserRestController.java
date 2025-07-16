package ru.ivanov.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.userservice.dto.UserDto;
import ru.ivanov.userservice.dto.request.LoginRequest;
import ru.ivanov.userservice.dto.request.RegistrationRequest;
import ru.ivanov.userservice.dto.request.UpdateUserRequest;
import ru.ivanov.userservice.security.UserDetailsImpl;
import ru.ivanov.userservice.service.UserService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('AUTH_SERVICE')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegistrationRequest request) {
        UserDto createdUser = userService.createUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/verify-credentials")
    @PreAuthorize("hasRole('AUTH_SERVICE')")
    public ResponseEntity<UserDto> verifyCredentials(@Valid @RequestBody LoginRequest request) {
        UserDto userDto = userService.verifyCredentials(request.username(), request.password());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('AUTH_SERVICE')")
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UUID userId = userDetails.getUserId();
        UserDto user = userService.getUser(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user);
    }

    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> updateUserPatch(
           @Valid @RequestBody UpdateUserRequest request,
           @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID userId = userDetails.getUserId();
        UserDto updatedUser = userService.updateUserPatch(userId, request);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UUID userId = userDetails.getUserId();
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}

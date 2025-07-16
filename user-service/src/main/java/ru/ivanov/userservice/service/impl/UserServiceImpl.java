package ru.ivanov.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.common.event.UserCreatedEvent;
import ru.ivanov.common.event.UserDeletedEvent;
import ru.ivanov.userservice.dto.UserDto;
import ru.ivanov.userservice.dto.request.RegistrationRequest;
import ru.ivanov.userservice.dto.request.UpdateUserRequest;
import ru.ivanov.userservice.exception.UserNotFoundException;
import ru.ivanov.userservice.exception.UsernameIsTakenException;
import ru.ivanov.userservice.mapper.UserMapper;
import ru.ivanov.userservice.model.Role;
import ru.ivanov.userservice.model.User;
import ru.ivanov.userservice.repository.UserRepository;
import ru.ivanov.userservice.service.RoleService;
import ru.ivanov.userservice.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static ru.ivanov.userservice.utils.MessageUtils.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Lazy
    @Autowired
    private UserService self;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public UserDto createUser(RegistrationRequest request) {
        if (existsByUsername(request.username())) {
            throw new UsernameIsTakenException(USERNAME_IS_ALREADY_TAKEN.formatted(request.username()));
        }

        Role roleUser = roleService.findByName("ROLE_USER");

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                List.of(roleUser)
        );

        User savedUser = userRepository.save(user);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                "user-created-event-topic",
                null,
                new UserCreatedEvent(savedUser.getId())
        );

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                System.out.println("Failed to send message " + exception.getMessage());
            } else {
                System.out.println("Message sent successfully, " + result.getRecordMetadata().toString());
            }
        });

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('AUTH_SERVICE')")
    public UserDto verifyCredentials(String username, String password) {
        User user = findUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("bad credentials");
        }

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('AUTH_SERVICE')")
    public UserDto getUser(UUID userId) {
        User user = findUserById(userId);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserPatch(UUID userId, UpdateUserRequest request) {
        User user = findUserById(userId);

        if (request.username() != null) {
            if (existsByUsername(request.username())) {
                throw new UsernameIsTakenException(USERNAME_IS_ALREADY_TAKEN.formatted(request.username()));
            }

            user.setUsername(request.username());
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }


    @Override
    @Transactional
    public void deleteUserById(UUID userId) {
        User user = findUserById(userId);
        userRepository.deleteById(userId);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                "user-deleted-event-topic",
                null,
                new UserDeletedEvent(userId)
        );

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                System.out.println("Failed to send message " + exception.getMessage());
            } else {
                System.out.println("Message sent successfully, " + result.getRecordMetadata().toString());
            }
        });
    }



    private User findUserById(UUID userId) {
        return  userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND.formatted(userId)));
    }

    private User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_USERNAME.formatted(username)));
    }

    private boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
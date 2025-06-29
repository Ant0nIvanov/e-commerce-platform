package ru.ivanov.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.userservice.dto.request.UpdateUserRequest;
import ru.ivanov.userservice.exception.UsernameIsTakenException;
import ru.ivanov.userservice.exception.UserNotFoundException;
import ru.ivanov.userservice.mapper.UserMapper;
import ru.ivanov.userservice.dto.UserDto;
import ru.ivanov.userservice.dto.request.RegistrationRequest;
import ru.ivanov.userservice.model.Role;
import ru.ivanov.userservice.model.User;
import ru.ivanov.userservice.repository.UserRepository;
import ru.ivanov.userservice.service.RoleService;
import ru.ivanov.userservice.service.UserService;
import ru.ivanov.userservice.service.UserValidationService;

import java.util.List;
import java.util.UUID;

import static ru.ivanov.userservice.utils.MessageUtils.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidationService userValidationService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @CachePut(value = "users", key = "#result.id")
    @Transactional
    public UserDto createUser(RegistrationRequest request) {
        if (userValidationService.existsByUsername(request.username())) {
            throw new UsernameIsTakenException(USERNAME_IS_ALREADY_TAKEN.formatted(request.username()));
        }

        Role roleUser = roleService.findByName("ROLE_USER");

        User user = new User(
                request.username(),
                request.hashedPassword(),
                request.firstName(),
                request.lastName(),
                List.of(roleUser)
        );

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
//    @Cacheable(
//            value = "userCredentials",
//            key = "T(ru.ivanov.userservice.utils.CacheUtils).generateCredentialsKey(#username, #hashedPassword)",
//            unless = "#result == null"
//    )
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('AUTH_SERVICE')")
    public UserDto verifyCredentials(String username, String password) {
        User user = findUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("bad credentials");
        }

        return userMapper.toDto(user);
    }

    @Cacheable(value = "userByUsername", key = "#username", unless = "#result == null")
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_USERNAME.formatted(username)));
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('AUTH_SERVICE')") // НО И ВОЗМОЖНО ЧТО USER ТОЖЕ
    public UserDto getUser(UUID userId) {
        User user = findUserById(userId);
        return userMapper.toDto(user);
    }

    @Override
    @CachePut(value = "users", key = "#result.id")
//    @CacheEvict(
//            value = "userCredentials",
//            key = "T(ru.ivanov.userservice.utils.CacheUtils).generateCredentialsKey()")
    @Transactional
    public UserDto updateUser(UUID userId, UpdateUserRequest request) {
        User user = findUserById(userId);

        if (!user.getUsername().equals(request.username())) {
            if (userValidationService.existsByUsername(request.username())) {
                throw new UsernameIsTakenException(USERNAME_IS_ALREADY_TAKEN.formatted(request.username()));
            }

            userValidationService.evictUsernameFromCache(user.getUsername());
            user.setUsername(request.username());
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(request.hashedPassword());

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "userCredentials", allEntries = true)
    })
    @Transactional
    public void deleteUserById(UUID userId) {
        User user = findUserById(userId);

        userRepository.deleteById(userId);
        userValidationService.evictUsernameFromCache(user.getUsername());
    }

    private User findUserById(UUID userId) {
        return  userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND.formatted(userId)));
    }
}
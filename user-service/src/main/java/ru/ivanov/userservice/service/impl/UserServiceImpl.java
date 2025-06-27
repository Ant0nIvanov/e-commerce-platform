package ru.ivanov.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.userservice.dto.request.UpdateUserRequest;
import ru.ivanov.userservice.exception.RoleNotFoundException;
import ru.ivanov.userservice.exception.UserAlreadyExistsException;
import ru.ivanov.userservice.exception.UserNotFoundException;
import ru.ivanov.userservice.mapper.UserMapper;
import ru.ivanov.userservice.dto.UserDto;
import ru.ivanov.userservice.dto.request.RegistrationRequest;
import ru.ivanov.userservice.model.Role;
import ru.ivanov.userservice.model.User;
import ru.ivanov.userservice.repository.RoleRepository;
import ru.ivanov.userservice.repository.UserRepository;
import ru.ivanov.userservice.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("");
        }

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("role not found"));

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                List.of(roleUser)
        );

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto verifyCredentials(String username, String password) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new BadCredentialsException("bad credentials");
//        }

        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUser(UUID userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        if (!user.getUsername().equals(request.username())) {
            boolean isUsernameFree = userRepository.existsByUsername(request.username());
            if (isUsernameFree) {
                throw new UserAlreadyExistsException("");
            }
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public void deleteUserById(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("user not found");
        }

        userRepository.deleteById(userId);
    }
}
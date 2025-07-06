package ru.ivanov.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserRepository userRepository;

    @Cacheable(value = "usernames", key = "#username", unless = "#result == false")
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @CacheEvict(value = "usernames", key = "#username")
    public void evictUsernameFromCache(String username) {}
}
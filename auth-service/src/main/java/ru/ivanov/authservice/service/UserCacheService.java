package ru.ivanov.authservice.service;

import org.hibernate.Cache;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cglib.core.internal.LoadingCache;
import ru.ivanov.authservice.client.UserServiceClient;
import ru.ivanov.authservice.dto.UserDto;

import java.util.UUID;

public class UserCacheService {
//    private final UserServiceClient userClient;
//    private final LoadingCache<UUID, UserDto> cache;
//
//    public UserCacheService(UserServiceClient userClient) {
//        this.userClient = userClient;
//        this.cache = CacheProperties.Caffeine.
//    }
}

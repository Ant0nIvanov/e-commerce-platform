package ru.ivanov.userservice.utils;

import org.springframework.util.DigestUtils;

public class CacheUtils {
    public static String generateCredentialsKey(String username, String password) {
        String passwordHash = DigestUtils.md5DigestAsHex(password.getBytes());
        return username + ":" + passwordHash;
    }
}
package ru.ivanov.cartservice.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {
    @Getter
    private final UUID userId;
    private final String username;
    private final List<GrantedAuthority> roles;

    public UserDetailsImpl(UUID userId, String username, List<GrantedAuthority> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }
}

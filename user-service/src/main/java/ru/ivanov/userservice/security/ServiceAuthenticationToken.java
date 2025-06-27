package ru.ivanov.userservice.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class ServiceAuthenticationToken extends AbstractAuthenticationToken {
    private final String serviceName;

    @Getter
    private final UUID userId;

    public ServiceAuthenticationToken(String serviceName, UUID userId,
                                      Collection<? extends GrantedAuthority> roles) {
        super(roles);
        this.serviceName = serviceName;
        this.userId = userId;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return serviceName;
    }
}

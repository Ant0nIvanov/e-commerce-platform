package ru.ivanov.authservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class LoginRequest {
    @JsonProperty("username")
    private final String username;

    @Getter
    @Setter
    @JsonProperty("password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

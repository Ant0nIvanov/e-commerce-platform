package ru.ivanov.userservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegistrationRequest(
        @JsonProperty("username")
        String username,

        @JsonProperty("firstName")
        String firstName,

        @JsonProperty("lastName")
        String lastName,

        @JsonProperty("password")
        String hashedPassword
) {
}
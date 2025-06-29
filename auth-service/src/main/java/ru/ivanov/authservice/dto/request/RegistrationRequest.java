package ru.ivanov.authservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class RegistrationRequest {
        @JsonProperty("username")
        private final String username;

        @JsonProperty("firstName")
        private final String firstName;

        @JsonProperty("lastName")
        private final String lastName;

        @Getter
        @Setter
        @JsonProperty("password")
        private String password;

    public RegistrationRequest(String username, String firstName, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
               "username='" + username + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", password='" + password + '\'' +
               '}';
    }
}

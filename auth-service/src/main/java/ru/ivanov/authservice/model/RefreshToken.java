package ru.ivanov.authservice.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private String token;

    public RefreshToken(UUID userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
package ru.ivanov.userservice.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import ru.ivanov.userservice.model.enums.SellerType;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "sellers")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SellerType type;

    private String displayName;


    public Seller(String username, String password, SellerType type, String displayName) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.displayName = displayName;
    }
}
package ru.ivanov.userservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ivanov.userservice.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findUserByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findUserById(UUID id);

    boolean existsByUsername(String username);
}
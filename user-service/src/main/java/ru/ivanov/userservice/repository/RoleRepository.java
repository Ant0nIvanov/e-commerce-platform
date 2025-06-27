package ru.ivanov.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ivanov.userservice.model.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
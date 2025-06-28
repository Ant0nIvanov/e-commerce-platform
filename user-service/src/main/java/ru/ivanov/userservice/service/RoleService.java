package ru.ivanov.userservice.service;

import ru.ivanov.userservice.model.Role;

public interface RoleService {
    Role findByName(String name);
}

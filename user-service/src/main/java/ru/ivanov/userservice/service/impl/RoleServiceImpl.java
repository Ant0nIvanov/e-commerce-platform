package ru.ivanov.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.userservice.exception.RoleNotFoundException;
import ru.ivanov.userservice.model.Role;
import ru.ivanov.userservice.repository.RoleRepository;
import ru.ivanov.userservice.service.RoleService;

import static ru.ivanov.userservice.utils.MessageUtils.ROLE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    @Override
    @Cacheable(value = "roles", key = "#name")
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND.formatted(name)));
    }
}

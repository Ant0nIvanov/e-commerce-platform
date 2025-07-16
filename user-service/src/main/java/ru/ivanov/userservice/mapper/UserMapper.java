package ru.ivanov.userservice.mapper;

import org.springframework.stereotype.Component;
import ru.ivanov.userservice.dto.UserDto;
import ru.ivanov.userservice.model.Role;
import ru.ivanov.userservice.model.User;

@Component
public class UserMapper {

    public UserDto toDto(User User) {
        return new UserDto(
          User.getId(),
          User.getFirstName(),
          User.getLastName(),
          User.getUsername(),
          User.getRoles().stream()
                  .map(Role::getName)
                  .toList()
        );
    }
}

package ru.ivanov.authservice.mapper;

import org.springframework.stereotype.Component;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.dto.UserSafeDto;

@Component
public class UserDtoMapper {

    public UserSafeDto toSafeDto(UserDto userDto) {
        return new UserSafeDto(
                userDto.id(),
                userDto.username(),
                userDto.firstName(),
                userDto.lastName(),
                userDto.roles().contains("ROLE_ADMIN")
        );
    }
}

package ru.ivanov.authservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.dto.request.LoginRequest;
import ru.ivanov.authservice.dto.request.RegistrationRequest;

@FeignClient(value = "user-service", url = "${user.service.url}")
public interface AuthInterface {

    @PostMapping()
    ResponseEntity<UserDto> createNewUser(RegistrationRequest request);

    @PostMapping("/verify-credentials")
    ResponseEntity<UserDto> verifyCredentials(LoginRequest request);

    @PostMapping()
    ResponseEntity<UserDto> getUserByID();
}

package dev.gmelon.moa.web.controller.auth;

import dev.gmelon.moa.web.service.auth.AuthService;
import dev.gmelon.moa.web.service.auth.dto.LoginRequest;
import dev.gmelon.moa.web.service.auth.dto.LoginResponse;
import dev.gmelon.moa.web.service.auth.dto.SocialLoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/social-login")
    public LoginResponse socialLogin(@RequestBody @Valid SocialLoginRequest loginRequest) {
        return authService.socialLogin(loginRequest);
    }

}

package dev.gmelon.moa.web.service.auth;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.storage.user.UserRepository;
import dev.gmelon.moa.support.JwtProvider;
import dev.gmelon.moa.support.MoaPasswordEncoder;
import dev.gmelon.moa.web.exception.BadRequestException;
import dev.gmelon.moa.web.service.auth.dto.LoginRequest;
import dev.gmelon.moa.web.service.auth.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final MoaPasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("error.auth.invalidEmailOrPassword"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("error.auth.invalidEmailOrPassword");
        }
        return LoginResponse.from(jwtProvider.createTokenWithSubject(user.getId()));
    }

}

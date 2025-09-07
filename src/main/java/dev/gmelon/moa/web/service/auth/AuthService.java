package dev.gmelon.moa.web.service.auth;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.storage.user.UserRepository;
import dev.gmelon.moa.support.JwtProvider;
import dev.gmelon.moa.support.MoaPasswordEncoder;
import dev.gmelon.moa.web.exception.BadRequestException;
import dev.gmelon.moa.web.service.auth.dto.LoginRequest;
import dev.gmelon.moa.web.service.auth.dto.LoginResponse;
import dev.gmelon.moa.web.service.auth.dto.SocialLoginRequest;
import dev.gmelon.moa.web.service.auth.social.SocialClients;
import dev.gmelon.moa.web.service.auth.social.dto.SocialLoginResponse;
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

    private final SocialClients socialClients;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("error.auth.invalidEmailOrPassword"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("error.auth.invalidEmailOrPassword");
        }
        return LoginResponse.from(jwtProvider.createTokenWithSubject(user.getId()));
    }

    public LoginResponse socialLogin(SocialLoginRequest loginRequest) {
        SocialLoginResponse socialLoginResponse
                = socialClients.requestAccountResponse(loginRequest.getJoinType(), loginRequest.getToken());

        User user = userRepository.findByEmail(socialLoginResponse.getEmail())
                .orElseGet(() -> {
                    User newUser = User.newSocialMember(
                            socialLoginResponse.getEmail(),
                            socialLoginResponse.getName(),
                            loginRequest.getJoinType()
                    );
                    return userRepository.save(newUser);
                });
        return LoginResponse.from(jwtProvider.createTokenWithSubject(user.getId()));
    }

}

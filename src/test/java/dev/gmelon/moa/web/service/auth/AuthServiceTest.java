package dev.gmelon.moa.web.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.storage.user.UserRepository;
import dev.gmelon.moa.support.JwtProvider;
import dev.gmelon.moa.support.MoaPasswordEncoder;
import dev.gmelon.moa.web.exception.BadRequestException;
import dev.gmelon.moa.web.service.auth.dto.LoginRequest;
import dev.gmelon.moa.web.service.auth.dto.LoginResponse;
import dev.gmelon.moa.web.service.auth.dto.SocialLoginRequest;
import dev.gmelon.moa.web.service.auth.social.SocialClients;
import dev.gmelon.moa.web.service.auth.social.dto.SocialLoginResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MoaPasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private SocialClients socialClients;

    @Test
    void login_whenEmailNotExists() {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("invalid@email.com")
                .build();
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("error.auth.invalidEmailOrPassword");
    }

    @Test
    void login_whenPasswordNotValid() {
        // given
        User user = User.builder()
                .email("valid@email.com")
                .password("encodedPassword")
                .build();
        LoginRequest request = LoginRequest.builder()
                .email(user.getEmail())
                .password("invalidRawPassword")
                .build();
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("error.auth.invalidEmailOrPassword");
    }

    @Test
    void login() {
        // given
        User user = User.builder()
                .email("valid@email.com")
                .password("encodedPassword")
                .build();
        LoginRequest request = LoginRequest.builder()
                .email(user.getEmail())
                .password("validRawPassword")
                .build();
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(jwtProvider.createTokenWithSubject(any())).willReturn("jwtToken");

        // when
        LoginResponse actual = authService.login(request);

        // then
        LoginResponse expected = LoginResponse.builder()
                .token("jwtToken")
                .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void socialLogin_whenSocialLoginFailed() {
        // given
        given(socialClients.requestAccountResponse(any(), any()))
                .willThrow(new BadRequestException("error.social.invalidToken"));
        SocialLoginRequest loginRequest = SocialLoginRequest.builder()
                .joinType(UserJoinType.APPLE)
                .token("invalidToken")
                .build();

        // when, then
        assertThatThrownBy(() -> authService.socialLogin(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("error.social.invalidToken");
    }

    @Test
    void socialLogin_whenUserNotExists() {
        // given
        SocialLoginRequest loginRequest = SocialLoginRequest.builder()
                .joinType(UserJoinType.APPLE)
                .token("validToken")
                .build();
        given(socialClients.requestAccountResponse(any(), any()))
                .willReturn(SocialLoginResponse.builder()
                        .email("user@user.com")
                        .name("user")
                        .build());
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());
        given(userRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        given(jwtProvider.createTokenWithSubject(any())).willReturn("jwtToken");

        // when
        LoginResponse actual = authService.socialLogin(loginRequest);

        // then
        then(userRepository).should().save(any(User.class));
        LoginResponse expected = LoginResponse.builder()
                .token("jwtToken")
                .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void socialLogin_whenUserExists() {
        // given
        SocialLoginRequest loginRequest = SocialLoginRequest.builder()
                .joinType(UserJoinType.APPLE)
                .token("validToken")
                .build();
        given(socialClients.requestAccountResponse(any(), any()))
                .willReturn(SocialLoginResponse.builder()
                        .email("user@user.com")
                        .name("user")
                        .build());
        given(userRepository.findByEmail(any()))
                .willReturn(Optional.of(User.builder()
                        .email("user@user.com")
                        .name("user")
                        .build()));
        given(jwtProvider.createTokenWithSubject(any())).willReturn("jwtToken");

        // when
        LoginResponse actual = authService.socialLogin(loginRequest);

        // then
        LoginResponse expected = LoginResponse.builder()
                .token("jwtToken")
                .build();
        assertThat(actual).isEqualTo(expected);
    }

}

package dev.gmelon.moa.web.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.storage.user.UserRepository;
import dev.gmelon.moa.support.JwtProvider;
import dev.gmelon.moa.support.MoaPasswordEncoder;
import dev.gmelon.moa.web.service.auth.dto.LoginRequest;
import dev.gmelon.moa.web.service.auth.dto.LoginResponse;
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

}

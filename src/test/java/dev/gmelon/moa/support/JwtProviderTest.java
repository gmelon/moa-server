package dev.gmelon.moa.support;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.gmelon.moa.TestConfig;
import dev.gmelon.moa.TestConfig.MutableClock;
import dev.gmelon.moa.web.exception.NoPermissionException;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import({JwtProvider.class, TestConfig.class})
@ExtendWith(SpringExtension.class)
public class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MutableClock clock;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void createTokenAndVerify_valid() {
        // given
        Long userId = 1L;
        String token = jwtProvider.createTokenWithSubject(userId);

        // when
        Long result = jwtProvider.verifyTokenAndGetSubject(token);

        // then
        assertEquals(userId, result);
    }

    @Test
    public void createTokenAndVerify_expiredToken() {
        // given
        Long userId = 1L;
        String token = jwtProvider.createTokenWithSubject(userId);
        
        clock.plus(Duration.ofMinutes(31));

        // when, then
        assertThatThrownBy(() -> jwtProvider.verifyTokenAndGetSubject(token))
            .isInstanceOf(NoPermissionException.class);
    }

    @Test
    public void createTokenAndVerify_invalidToken() {
        // given
        String invalidToken = "invalid";

        // when, then
        assertThatThrownBy(() -> jwtProvider.verifyTokenAndGetSubject(invalidToken))
            .isInstanceOf(NoPermissionException.class);
    }

}

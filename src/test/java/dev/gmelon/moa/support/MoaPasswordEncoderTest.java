package dev.gmelon.moa.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(MoaPasswordEncoder.class)
@ExtendWith(SpringExtension.class)
class MoaPasswordEncoderTest {

    @Autowired
    private MoaPasswordEncoder moaPasswordEncoder;

    @Test
    void matches_valid() {
        // given
        String rawPassword = "password";
        String encodedPassword = moaPasswordEncoder.encode(rawPassword);

        // when
        boolean result = moaPasswordEncoder.matches(rawPassword, encodedPassword);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void matches_invalid() {
        // given
        String rawPassword = "password";
        String encodedPassword = moaPasswordEncoder.encode(rawPassword) + "invalid";

        // when
        boolean result = moaPasswordEncoder.matches(rawPassword, encodedPassword);

        // then
        assertThat(result).isFalse();
    }

}

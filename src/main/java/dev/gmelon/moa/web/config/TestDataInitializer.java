package dev.gmelon.moa.web.config;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.storage.user.UserRepository;
import dev.gmelon.moa.storage.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod")
@RequiredArgsConstructor
@Configuration
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(final String... args) throws Exception {
        userRepository.save(createTestAdminUser());
    }

    private User createTestAdminUser() {
        return User.builder()
            .email("hello@gmelon.dev")
            .password("password")
            .name("gmelon")
            .role(UserRole.ADMIN)
            .joinType(UserJoinType.EMAIL)
            .build();
    }

}

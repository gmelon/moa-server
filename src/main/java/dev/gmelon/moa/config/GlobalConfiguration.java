package dev.gmelon.moa.config;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class GlobalConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}

package dev.gmelon.moa.web.config;

import dev.gmelon.moa.web.config.interceptor.JwtUserSessionInterceptor;
import dev.gmelon.moa.web.config.resolver.UserArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    private final JwtUserSessionInterceptor jwtUserSessionInterceptor;

    private final UserArgumentResolver userArgumentResolver;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtUserSessionInterceptor);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

}

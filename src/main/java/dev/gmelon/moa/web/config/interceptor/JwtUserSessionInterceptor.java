package dev.gmelon.moa.web.config.interceptor;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.storage.user.UserRepository;
import dev.gmelon.moa.support.JwtProvider;
import dev.gmelon.moa.web.config.UserSessionHolder;
import dev.gmelon.moa.web.exception.NoPermissionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JwtUserSessionInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserSessionHolder userSessionHolder;

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    public JwtUserSessionInterceptor(final UserSessionHolder userSessionHolder,
                                     final JwtProvider jwtProvider,
                                     final UserRepository userRepository) {
        this.userSessionHolder = userSessionHolder;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!isValidAuthorizationHeader(authorizationHeader)) {
            return true;
        }
        Long userId = jwtProvider.verifyTokenAndGetSubject(extractJwtToken(authorizationHeader));
        return loadUserAndSetSession(userId);
    }

    private boolean isValidAuthorizationHeader(String authorizationHeader) {
        return StringUtils.hasText(authorizationHeader)
                && authorizationHeader.startsWith(BEARER_PREFIX)
                && authorizationHeader.length() > BEARER_PREFIX.length();
    }

    private String extractJwtToken(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }

    private boolean loadUserAndSetSession(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            log.error("User not found for ID: {}", userId);
            throw new NoPermissionException("error.user.noPermission");
        }

        userSessionHolder.setUser(userOptional.get());
        return true;
    }
}

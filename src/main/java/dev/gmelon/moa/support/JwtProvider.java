package dev.gmelon.moa.support;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier.BaseVerification;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import dev.gmelon.moa.web.exception.NoPermissionException;
import jakarta.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final MessageSource messageSource;

    private final Clock clock;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMinutes:30}")
    private int expirationMinutes;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secret);
    }

    public String createTokenWithSubject(final Long userId) {
        try {
            return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(getExpiresAt())
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            log.error("Error creating JWT.", exception);
            throw exception;
        }
    }

    private Instant getExpiresAt() {
        return LocalDateTime.now(clock)
            .plusMinutes(expirationMinutes)
            .atZone(clock.getZone())
            .toInstant();
    }

    public Long verifyTokenAndGetSubject(final String token) {
        try {
            JWTVerifier verifier = ((BaseVerification) JWT.require(algorithm)).build(clock);
            DecodedJWT decodedJWT = verifier.verify(token);
            return Long.parseLong(decodedJWT.getSubject());
        } catch (JWTVerificationException exception) {
            throw new NoPermissionException(
                messageSource.getMessage("error.jwt.invalidToken", null, getLocale()));
        }
    }

}

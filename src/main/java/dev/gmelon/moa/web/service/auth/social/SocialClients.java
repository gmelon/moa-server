package dev.gmelon.moa.web.service.auth.social;

import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.web.service.auth.social.dto.SocialLoginResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SocialClients {

    private final List<SocialClient> clients;

    public SocialLoginResponse requestAccountResponse(UserJoinType type, String token) {
        return clients.stream()
                .filter(client -> client.supports(type))
                .findAny()
                .map(client -> client.requestAccountResponse(token))
                .orElseThrow(() -> new IllegalArgumentException("Invalid UserJoinType: " + type));
    }

    public void revokeToken(UserJoinType type, Long targetId, String token) {
        clients.stream()
                .filter(client -> client.supports(type))
                .findAny()
                .ifPresentOrElse(client -> client.revokeToken(targetId, token),
                        () -> {
                            throw new IllegalArgumentException("Invalid UserJoinType: " + type);
                        });
    }

}

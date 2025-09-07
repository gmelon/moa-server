package dev.gmelon.moa.web.service.auth.social;

import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.web.service.auth.social.dto.SocialLoginResponse;

public interface SocialClient {

    boolean supports(UserJoinType joinType);

    SocialLoginResponse requestAccountResponse(String token);

    void revokeToken(Long targetId, String token);

}

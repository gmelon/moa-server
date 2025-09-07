package dev.gmelon.moa.web.service.auth.social;

import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.web.service.auth.social.dto.SocialAccountResponse;

public interface SocialClient {

    boolean supports(UserJoinType joinType);

    SocialAccountResponse requestAccountResponse(String token);

    void revokeToken(Long targetId, String token);

}

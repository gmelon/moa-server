package dev.gmelon.moa.web.service.auth.dto;

import dev.gmelon.moa.storage.user.UserJoinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SocialLoginRequest {

    @NotNull
    private UserJoinType joinType;

    @NotBlank
    private String token;

}

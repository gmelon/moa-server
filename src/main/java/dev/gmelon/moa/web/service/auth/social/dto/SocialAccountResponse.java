package dev.gmelon.moa.web.service.auth.social.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SocialAccountResponse {

    private String email;

    private String name;

}

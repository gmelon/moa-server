package dev.gmelon.moa.web.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginResponse {

    private String token;

    public static LoginResponse from(String token) {
        return LoginResponse.builder()
                .token(token)
                .build();
    }

}

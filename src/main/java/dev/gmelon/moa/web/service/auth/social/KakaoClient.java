package dev.gmelon.moa.web.service.auth.social;

import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.web.exception.BadRequestException;
import dev.gmelon.moa.web.service.auth.social.dto.SocialLoginResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class KakaoClient implements SocialClient {

    private final WebClient webClient;

    @Value("${auth.social.kakao.base_url}")
    private String baseUrl;

    @Override
    public boolean supports(UserJoinType joinType) {
        return joinType == UserJoinType.KAKAO;
    }

    @Override
    public SocialLoginResponse requestAccountResponse(String token) {
        String response = webClient.post()
                .uri(baseUrl + "/v2/user/me")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject kakaoAccount = jsonObject.getJSONObject("kakao_account");
            String email = kakaoAccount.getString("email");
            return SocialLoginResponse.builder()
                    .email(email)
                    .name(email)
                    .build();
        } catch (JSONException exception) {
            throw new BadRequestException("error.social.invalidToken");
        }
    }

    @Override
    public void revokeToken(Long targetId, String token) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", targetId.toString());

        webClient.post()
                .uri(baseUrl + "/v1/user/unlink")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

}

package dev.gmelon.moa.web.service.auth.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.web.exception.BadRequestException;
import dev.gmelon.moa.web.exception.InternalServerException;
import dev.gmelon.moa.web.service.auth.social.dto.ApplePublicKeyResponse;
import dev.gmelon.moa.web.service.auth.social.dto.ApplePublicKeyResponse.Key;
import dev.gmelon.moa.web.service.auth.social.dto.SocialAccountResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class AppleClient implements SocialClient {


    private static final ResourcePatternResolver resourcePatternResolver;

    private final WebClient webClient;

    @Value("${auth.social.apple.base_url}")
    private String baseUrl;

    private final ObjectMapper objectMapper;

    static {
        resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader());
    }

    @Value("${apple-key-path}")
    private String appleKeyPath;

    @Value("${auth.social.apple.client_id}")
    private String clientId;

    @Value("${auth.social.apple.team_id}")
    private String teamId;

    @Value("${auth.social.apple.key_id}")
    private String keyId;

    @Override
    public boolean supports(UserJoinType joinType) {
        return joinType == UserJoinType.APPLE;
    }

    @Override
    public SocialAccountResponse requestAccountResponse(String token) {
        String idToken = request(token, "id_token");
        try {
            String idHeaderToken = idToken.split("\\.")[0];
            String decodedIdHeaderToken = new String(Base64.getDecoder().decode(idHeaderToken));
            Map<String, String> idHeader = objectMapper.readValue(decodedIdHeaderToken, Map.class);

            Claims payload = Jwts.parser()
                    .verifyWith(publicKeyOf(idHeader.get("kid"), idHeader.get("alg")))
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();
            String email = payload.get("email", String.class);
            return SocialAccountResponse.builder()
                    .email(email)
                    .name(email)
                    .build();
        } catch (JwtException | JsonProcessingException exception) {
            throw new BadRequestException("error.social.invalidToken");
        }
    }

    @Override
    public void revokeToken(Long targetId, String token) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret());
        body.add("token", request(token, "access_token"));

        webClient.post()
                .uri(baseUrl + "/revoke")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

    private String request(String token, String field) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret());
        body.add("code", token);
        body.add("grant_type", "authorization_code");

        String response = webClient.post()
                .uri(baseUrl + "/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString(field);
        } catch (JSONException exception) {
            throw new BadRequestException("error.social.invalidToken");
        }
    }

    private PublicKey publicKeyOf(String kid, String alg) {
        ApplePublicKeyResponse response = webClient.get()
                .uri(baseUrl + "/keys")
                .retrieve()
                .bodyToMono(ApplePublicKeyResponse.class)
                .block();

        Key key = response.matchedKeyOf(kid, alg);

        Decoder decoder = Base64.getUrlDecoder();
        byte[] nBytes = decoder.decode(key.getN());
        byte[] eBytes = decoder.decode(key.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            return keyFactory.generatePublic(new RSAPublicKeySpec(n, e));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new InternalServerException("Failed to generate Apple public key");
        }
    }

    private String clientSecret() {
        return Jwts.builder()
                .header()
                .keyId(keyId)
                .and()
                .issuer(teamId)
                .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .expiration(Timestamp.valueOf(LocalDateTime.now().plusMinutes(30)))
                .audience().add("https://appleid.apple.com")
                .and()
                .subject(clientId)
                .signWith(privateKey(), SIG.ES256)
                .compact();
    }

    private PrivateKey privateKey() {
        byte[] keyBytes;
        try {
            keyBytes = resourcePatternResolver.getResource(appleKeyPath).getInputStream().readAllBytes();
        } catch (IOException exception) {
            throw new InternalServerException("Failed to load Apple private key");
        }

        String key = new String(keyBytes).replaceAll("\\s+", "");
        try {
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new InternalServerException("Failed to generate Apple private key");
        }
    }

}

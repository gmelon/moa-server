package dev.gmelon.moa.web.service.auth.social.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApplePublicKeyResponse {

    private List<Key> keys;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Key {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }

    public Key matchedKeyOf(String kid, String alg) {
        return this.keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Kid and alg not found in Apple Public Key list. kid: " + kid + ", alg: " + alg));
    }

}

package com.example.community.auth.security;

import com.example.community.member.domain.Member;
import com.example.community.member.domain.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    // JWT 서명에 사용할 알고리즘이다.
    // HS256은 서버가 가진 하나의 secret key로 토큰을 서명하고 검증하는 방식이다.
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    // Java 객체를 JSON 문자열로 바꾸거나,
    // JSON 문자열을 다시 Java Map으로 바꾸기 위해 사용한다.
    // JWT의 header와 payload는 JSON 형태이기 때문에 필요하다.
    private final ObjectMapper objectMapper = new ObjectMapper();

    // application.properties에 적어둔 jwt.secret 값을 가져온다.
    // 이 값은 JWT가 위조되었는지 확인하는 서명 키로 사용된다.
    @Value("${jwt.secret}")
    private String secret;

    // application.properties에 적어둔 jwt.expiration 값을 가져온다.
    // 토큰이 몇 밀리초 동안 유효한지 정하는 값이다.
    @Value("${jwt.expiration}")
    private long expirationMillis;

    // 문자열 secret을 실제 HMAC 서명에 사용할 byte 배열로 바꿔서 저장한다.
    private byte[] secretBytes;

    @PostConstruct
    public void init() {
        // Bean 생성 후 secret 값을 byte[]로 변환해둔다.
        // 매번 토큰을 만들 때마다 변환하지 않으려고 미리 준비하는 것이다.
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String createToken(Member member) {
        try {
            // JWT header 부분이다.
            // alg는 어떤 알고리즘으로 서명했는지,
            // typ은 이 문자열이 JWT라는 것을 의미한다.
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            long now = Instant.now().toEpochMilli();
            long expiration = now + expirationMillis;

            // JWT payload 부분이다.
            // 로그인한 사용자 정보를 담는다.
            // 비밀번호 같은 민감한 정보는 절대 넣으면 안 된다.
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("memberId", member.getId());
            payload.put("email", member.getEmail());
            payload.put("nickname", member.getNickname());
            payload.put("role", member.getRole().name());
            payload.put("iat", now);
            payload.put("exp", expiration);

            // header와 payload를 JSON으로 바꾼 뒤,
            // JWT 규칙에 맞게 Base64Url 인코딩한다.
            String encodedHeader = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsBytes(payload));

            // 서명 전 토큰 형태다.
            // header.payload 구조로 만든다.
            String unsignedToken = encodedHeader + "." + encodedPayload;

            // 위조 여부를 확인할 수 있도록 서명을 만든다.
            String signature = createSignature(unsignedToken);

            // 최종 JWT 형식이다.
            // header.payload.signature
            return unsignedToken + "." + signature;

        } catch (Exception e) {
            throw new IllegalStateException("JWT 토큰 생성에 실패했습니다.");
        }
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");

            // JWT는 header, payload, signature 세 부분이어야 한다.
            if (parts.length != 3) {
                return false;
            }

            String unsignedToken = parts[0] + "." + parts[1];

            // 서버가 같은 header.payload로 다시 서명을 만들어본다.
            String expectedSignature = createSignature(unsignedToken);

            // 토큰에 들어있는 서명과 서버가 다시 만든 서명이 다르면
            // 토큰이 위조되었거나 secret key가 다른 것이다.
            if (!expectedSignature.equals(parts[2])) {
                return false;
            }

            Map<String, Object> payload = getPayload(token);
            long exp = Long.parseLong(payload.get("exp").toString());

            // 현재 시간이 만료 시간보다 뒤라면 사용할 수 없는 토큰이다.
            return Instant.now().toEpochMilli() <= exp;

        } catch (Exception e) {
            return false;
        }
    }

    public CustomUserPrincipal getPrincipal(String token) {
        // JWT payload에서 사용자 정보를 꺼낸다.
        Map<String, Object> payload = getPayload(token);

        Long memberId = Long.valueOf(payload.get("memberId").toString());
        String email = payload.get("email").toString();
        String nickname = payload.get("nickname").toString();
        Role role = Role.valueOf(payload.get("role").toString());

        // Spring Security에서 사용할 로그인 사용자 객체로 변환한다.
        return new CustomUserPrincipal(memberId, email, nickname, role);
    }

    private Map<String, Object> getPayload(String token) {
        try {
            String[] parts = token.split("\\.");

            // 두 번째 부분이 payload다.
            // Base64Url 디코딩하면 JSON 문자열이 나온다.
            String payloadJson = new String(
                    base64UrlDecode(parts[1]),
                    StandardCharsets.UTF_8
            );

            // JSON 문자열을 Map<String, Object>로 변환한다.
            return objectMapper.readValue(
                    payloadJson,
                    new TypeReference<Map<String, Object>>() {
                    }
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("JWT 토큰 정보를 읽을 수 없습니다.");
        }
    }

    private String createSignature(String unsignedToken) {
        try {
            // HMAC-SHA256 알고리즘을 사용하는 Mac 객체를 만든다.
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            // secret key를 HMAC 서명용 키 객체로 바꾼다.
            SecretKeySpec keySpec = new SecretKeySpec(secretBytes, HMAC_ALGORITHM);

            mac.init(keySpec);

            // header.payload 문자열을 secret key로 서명한다.
            byte[] signatureBytes = mac.doFinal(
                    unsignedToken.getBytes(StandardCharsets.UTF_8)
            );

            // JWT 규칙에 맞게 Base64Url 인코딩한다.
            return base64UrlEncode(signatureBytes);

        } catch (Exception e) {
            throw new IllegalStateException("JWT 서명 생성에 실패했습니다.");
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        // JWT는 일반 Base64가 아니라 URL-safe Base64를 사용한다.
        // 그리고 padding 문자 '='를 제거한다.
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private byte[] base64UrlDecode(String value) {
        // Base64Url 문자열을 다시 byte[]로 되돌린다.
        return Base64.getUrlDecoder().decode(value);
    }
}
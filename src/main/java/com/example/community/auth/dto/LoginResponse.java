package com.example.community.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    // 로그인 성공 후 클라이언트에게 내려줄 JWT 토큰
    // 클라이언트는 이 값을 저장해두고 인증이 필요한 API 요청마다 Authorization 헤더에 담아 보낸다.
    private final String accessToken;

    // 토큰 타입
    // 보통 JWT 인증에서는 Bearer 타입을 사용한다.
    private final String tokenType;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
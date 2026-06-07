package com.example.community.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    // 로그인 요청에서 받을 이메일
    // MemberRepository에서 회원을 찾을 때 사용한다.
    private String email;

    // 로그인 요청에서 받을 비밀번호
    // 저장된 암호화 비밀번호와 비교할 때 사용한다.
    private String password;
}
// 로그인 요청 데이터를 받는 DTO

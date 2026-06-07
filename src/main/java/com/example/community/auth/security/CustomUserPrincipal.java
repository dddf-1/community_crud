package com.example.community.auth.security;

import com.example.community.member.domain.Role;
import lombok.Getter;

@Getter
public class CustomUserPrincipal {

    // JWT에서 꺼낸 로그인 회원 ID
    // 게시글 작성자와 로그인 사용자가 같은지 비교할 때 사용한다.
    private final Long memberId;

    // 로그인 회원 이메일
    // 지금은 화면 응답에 꼭 필요하지 않지만, 인증된 사용자 식별 정보로 보관한다.
    private final String email;

    // 로그인 회원 닉네임
    // 추후 "내 정보 조회" 같은 기능에서 사용할 수 있다.
    private final String nickname;

    // 로그인 회원 권한
    // USER, ADMIN 같은 권한을 Spring Security 권한으로 연결할 때 사용한다.
    private final Role role;

    public CustomUserPrincipal(Long memberId, String email, String nickname, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }
}
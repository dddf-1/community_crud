package com.example.community.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    // 회원가입 요청에서 받을 이메일
    // 로그인할 때 ID처럼 사용된다.
    private String email;

    // 회원가입 요청에서 받을 비밀번호
    // 이 값은 그대로 저장하지 않고 AuthService에서 암호화해서 저장한다.
    private String password;

    // 회원가입 요청에서 받을 닉네임
    // 게시글 작성자 표시나 내 정보 조회 등에 사용할 수 있다.
    private String nickname;
}
// 회원가입 요청을 받는 DTO
// DTO인데 setter 안쓰는 이유
// -> setter를 쓰면 값이 의도적으로 변경될 수 있어 생략함.

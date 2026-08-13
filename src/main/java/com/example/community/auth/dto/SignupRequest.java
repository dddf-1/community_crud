package com.example.community.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class SignupRequest {

    // 회원가입 요청에서 받을 이메일
    // 로그인할 때 ID처럼 사용된다.
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // 회원가입 요청에서 받을 비밀번호
    // 이 값은 그대로 저장하지 않고 AuthService에서 암호화해서 저장한다.
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "비밀번호는 8~20자이며 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    // 회원가입 요청에서 받을 닉네임
    // 게시글 작성자 표시나 내 정보 조회 등에 사용할 수 있다.
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임에는 한글, 영문, 숫자만 사용할 수 있습니다.")
    private String nickname;

    private String profileImageUrl;
}
// 회원가입 요청을 받는 DTO
// DTO인데 setter 안쓰는 이유
// -> setter를 쓰면 값이 의도적으로 변경될 수 있어 생략함.

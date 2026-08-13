package com.example.community.auth.controller;

import com.example.community.auth.dto.LoginRequest;
import com.example.community.auth.dto.LoginResponse;
import com.example.community.auth.dto.SignupRequest;
import com.example.community.auth.service.AuthService;
import com.example.community.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.community.auth.security.CustomUserPrincipal;
import com.example.community.auth.security.JwtTokenProvider;
import com.example.community.member.domain.Member;
import com.example.community.member.dto.MemberResponse;
import com.example.community.member.repository.MemberRepository;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/auth", "/v1/auth"})
public class AuthController {

    // 인증 관련 비즈니스 로직은 Service가 담당한다.
    // Controller는 요청을 받고 응답을 반환하는 역할에 집중한다.
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Value("${app.auth.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.auth.cookie-same-site}")
    private String cookieSameSite;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signup(@Valid @RequestBody SignupRequest request) {

        // 회원가입 요청을 Service로 넘긴다.
        // 비밀번호 암호화, 이메일 중복 확인, 회원 저장은 AuthService에서 처리한다.
        authService.signup(request);

        return ApiResponse.success("회원가입 성공", null);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

        // 로그인 성공 시 JWT 토큰을 응답으로 받는다.
        // 기존 세션 방식과 달리 HttpSession을 받지 않는다.
        LoginResponse response = authService.login(request);

        ResponseCookie cookie = authCookie(response.getAccessToken(), jwtTokenProvider.getExpirationMillis());
        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(ApiResponse.success("로그인 성공", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {

        // JWT 방식에서는 서버 세션을 무효화하지 않는다.
        // 클라이언트가 저장한 토큰을 삭제하면 로그아웃이 된다.
        authService.logout();

        return ResponseEntity.ok()
                .header("Set-Cookie", authCookie("", 0).toString())
                .body(ApiResponse.success("로그아웃 성공", null));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<MemberResponse>> checkAuth(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Member member = memberRepository.findByMemberIdAndDeletedAtIsNull(principal.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return ResponseEntity.ok(ApiResponse.success("인증된 사용자입니다.", MemberResponse.from(member)));
    }

    private ResponseCookie authCookie(String token, long maxAgeMillis) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMillis))
                .build();
    }
}
// 인정과 관련된 것을 요청?받음.
// 컨트롤러는 요청만 받고 실제 처리는 AuthService에서 함

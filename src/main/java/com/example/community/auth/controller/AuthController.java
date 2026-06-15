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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    // 인증 관련 비즈니스 로직은 Service가 담당한다.
    // Controller는 요청을 받고 응답을 반환하는 역할에 집중한다.
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signup(@RequestBody SignupRequest request) {

        // 회원가입 요청을 Service로 넘긴다.
        // 비밀번호 암호화, 이메일 중복 확인, 회원 저장은 AuthService에서 처리한다.
        authService.signup(request);

        return ApiResponse.success("회원가입 성공", null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {

        // 로그인 성공 시 JWT 토큰을 응답으로 받는다.
        // 기존 세션 방식과 달리 HttpSession을 받지 않는다.
        LoginResponse response = authService.login(request);

        return ApiResponse.success("로그인 성공", response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {

        // JWT 방식에서는 서버 세션을 무효화하지 않는다.
        // 클라이언트가 저장한 토큰을 삭제하면 로그아웃이 된다.
        authService.logout();

        return ApiResponse.success("로그아웃 성공. 클라이언트에서 토큰을 삭제하세요.", null);
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Void>> checkAuth() {
        return ResponseEntity.ok(ApiResponse.success("인증된 사용자입니다.", null));
    }
}
// 인정과 관련된 것을 요청?받음.
// 컨트롤러는 요청만 받고 실제 처리는 AuthService에서 함
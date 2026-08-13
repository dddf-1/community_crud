package com.example.community.member.controller;

import com.example.community.auth.security.CustomUserPrincipal;
import com.example.community.global.ApiResponse;
import com.example.community.global.file.FileStorageService;
import com.example.community.member.dto.MemberResponse;
import com.example.community.member.dto.MemberUpdateRequest;
import com.example.community.member.dto.PasswordUpdateRequest;
import com.example.community.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/users", "/v1/users"})
public class MemberController {

    private final MemberService memberService;
    private final FileStorageService fileStorageService;

    @GetMapping("/email/check")
    public ApiResponse<Void> checkEmail(@RequestParam String email) {
        memberService.checkEmailAvailable(email);
        return ApiResponse.success("사용 가능한 이메일입니다.", null);
    }

    @GetMapping("/nickname/check")
    public ApiResponse<Void> checkNickname(@RequestParam String nickname) {
        memberService.checkNicknameAvailable(nickname);
        return ApiResponse.success("사용 가능한 닉네임입니다.", null);
    }

    @PostMapping("/upload/profile-image")
    public ApiResponse<Map<String, String>> uploadProfileImage(
            @RequestParam("profileImage") MultipartFile profileImage
    ) {
        String url = fileStorageService.saveImage(profileImage);
        return ApiResponse.success("프로필 이미지 업로드 성공", Map.of("profileImageUrl", url));
    }

    @GetMapping("/me")
    public ApiResponse<MemberResponse> getMe(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.success("회원 정보 조회 성공", memberService.getMember(principal.getMemberId()));
    }

    @PutMapping("/me")
    public ApiResponse<MemberResponse> updateMe(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        return ApiResponse.success("회원 정보 수정 성공", memberService.updateMember(principal.getMemberId(), request));
    }

    @PatchMapping("/me/password")
    public ApiResponse<Void> updatePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        memberService.updatePassword(principal.getMemberId(), request);
        return ApiResponse.success("비밀번호 수정 성공", null);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal CustomUserPrincipal principal) {
        memberService.withdraw(principal.getMemberId());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 성공", null));
    }
}

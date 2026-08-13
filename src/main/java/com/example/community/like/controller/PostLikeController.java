package com.example.community.like.controller;

import com.example.community.auth.security.CustomUserPrincipal;
import com.example.community.global.ApiResponse;
import com.example.community.like.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/posts/{postId}/likes", "/v1/posts/{postId}/likes"})
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping
    public ApiResponse<Map<String, Long>> like(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        long likeCount = postLikeService.like(postId, principal.getMemberId());
        return ApiResponse.success("좋아요 성공", Map.of("likeCount", likeCount));
    }

    @DeleteMapping
    public ApiResponse<Map<String, Long>> unlike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        long likeCount = postLikeService.unlike(postId, principal.getMemberId());
        return ApiResponse.success("좋아요 취소 성공", Map.of("likeCount", likeCount));
    }
}

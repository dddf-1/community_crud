package com.example.community.comment.controller;

import com.example.community.auth.security.CustomUserPrincipal;
import com.example.community.comment.dto.CommentRequest;
import com.example.community.comment.dto.CommentResponse;
import com.example.community.comment.service.CommentService;
import com.example.community.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/posts/{postId}/comments", "/v1/posts/{postId}/comments"})
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ApiResponse.success("댓글 목록 조회 성공", commentService.getComments(postId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody CommentRequest request
    ) {
        return ApiResponse.success(
                "댓글 생성 성공",
                commentService.createComment(postId, principal.getMemberId(), request)
        );
    }

    @PatchMapping("/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody CommentRequest request
    ) {
        return ApiResponse.success(
                "댓글 수정 성공",
                commentService.updateComment(postId, commentId, principal.getMemberId(), request)
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        commentService.deleteComment(postId, commentId, principal.getMemberId());
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 성공", null));
    }
}

package com.example.community.post.controller;

import com.example.community.auth.security.CustomUserPrincipal;
import com.example.community.global.ApiResponse;
import com.example.community.post.dto.PostCreateRequest;
import com.example.community.post.dto.PostResponse;
import com.example.community.post.dto.PostUpdateRequest;
import com.example.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @RequestBody PostCreateRequest request,

            // JwtAuthenticationFilter가 SecurityContextHolder에 저장한 로그인 사용자 정보가 여기로 들어온다.
            // 기존 세션 방식의 HttpSession 대신 사용하는 부분이다.
            @AuthenticationPrincipal CustomUserPrincipal loginMember
    ) {
        PostResponse response = postService.createPost(
                loginMember.getMemberId(),
                request
        );

        return ApiResponse.success("게시글 생성 성공", response);
    }

    @GetMapping
    public ApiResponse<List<PostResponse>> getPosts() {

        // 게시글 목록 조회는 로그인하지 않아도 가능하다.
        // 그래서 이 메서드에는 @AuthenticationPrincipal이 필요 없다.
        List<PostResponse> response = postService.getPosts();

        return ApiResponse.success("게시글 목록 조회 성공", response);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {

        // 게시글 단건 조회도 로그인하지 않아도 가능하다.
        PostResponse response = postService.getPost(postId);

        return ApiResponse.success("게시글 조회 성공", response);
    }

    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request,

            // JWT 인증에 성공한 사용자 정보
            // 이 memberId와 게시글 작성자 memberId를 Service에서 비교한다.
            @AuthenticationPrincipal CustomUserPrincipal loginMember
    ) {
        PostResponse response = postService.updatePost(
                postId,
                loginMember.getMemberId(),
                request
        );

        return ApiResponse.success("게시글 수정 성공", response);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable Long postId,

            // 삭제도 작성자 본인만 가능해야 하므로 로그인 사용자 정보가 필요하다.
            @AuthenticationPrincipal CustomUserPrincipal loginMember
    ) {
        postService.deletePost(
                postId,
                loginMember.getMemberId()
        );
    }
}
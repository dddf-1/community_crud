package com.example.community.post.controller;

import com.example.community.auth.security.CustomUserPrincipal;
import com.example.community.global.ApiResponse;
import com.example.community.global.file.FileStorageService;
import com.example.community.post.dto.PostCreateRequest;
import com.example.community.post.dto.PostResponse;
import com.example.community.post.dto.PostUpdateRequest;
import com.example.community.post.service.PostService;
import com.example.community.post.dto.PostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final FileStorageService fileStorageService;

    @PostMapping("/upload/attach-file")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAttachFile(
            @RequestParam("postFile") MultipartFile postFile
    ) {
        String fileUrl = fileStorageService.save(postFile);

        return ResponseEntity.ok(
                ApiResponse.success("게시글 이미지 업로드 성공", Map.of("fileUrl", fileUrl))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody PostCreateRequest request,

            // JwtAuthenticationFilter가 SecurityContextHolder에 저장한 로그인 사용자 정보가 여기로 들어온다.
            // 기존 세션 방식의 HttpSession 대신 사용하는 부분이다.
            @AuthenticationPrincipal CustomUserPrincipal loginMember
    ) {
        PostResponse response = postService.createPost(
                loginMember.getMemberId(),
                request
        );
        // 게시글 URI 생성
        URI location = URI.create("/api/posts/" + response.getId());
        return ResponseEntity.created(location)
                .body(ApiResponse.success("게시글 생성 성공", response));

        //return ApiResponse.success("게시글 생성 성공", response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Slice<PostListResponse>>> getPosts(Pageable pageable) {

        
        Slice<PostListResponse> response = postService.getPosts(pageable);

        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {

        // 게시글 단건 조회도 로그인하지 않아도 가능하다.
        PostResponse response = postService.getPost(postId);

        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
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

        return ResponseEntity.ok(ApiResponse.success("게시글 수정 성공", response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,

            // 삭제도 작성자 본인만 가능해야 하므로 로그인 사용자 정보가 필요하다.
            @AuthenticationPrincipal CustomUserPrincipal loginMember
    ) {
        postService.deletePost(
                postId,
                loginMember.getMemberId()
        );

        return ResponseEntity.noContent().build();
    }
}
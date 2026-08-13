package com.example.community.post.service;

import com.example.community.comment.repository.CommentRepository;
import com.example.community.global.ApiException;
import com.example.community.like.repository.PostLikeRepository;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import com.example.community.post.domain.Post;
import com.example.community.post.dto.PostCreateRequest;
import com.example.community.post.dto.PostListResponse;
import com.example.community.post.dto.PostResponse;
import com.example.community.post.dto.PostUpdateRequest;
import com.example.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostResponse createPost(Long memberId, PostCreateRequest request) {
        Member member = memberRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        validateUploadUrl(request.getAttachFileUrl());
        Post savedPost = postRepository.save(new Post(
                member,
                request.getTitle(),
                request.getContent(),
                request.getAttachFileUrl()
        ));
        return PostResponse.from(savedPost, 0, 0, false);
    }

    public Slice<PostListResponse> getPosts(Pageable pageable) {
        return postRepository.findPostList(pageable);
    }

    public Slice<PostListResponse> searchPosts(String keyword, String sort, Pageable pageable) {
        if (keyword == null || keyword.trim().length() < 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_SEARCH_KEYWORD", "검색어는 2글자 이상 입력해주세요.");
        }
        String normalizedKeyword = keyword.trim();
        return "relevance".equalsIgnoreCase(sort)
                ? postRepository.searchRelevant(normalizedKeyword, pageable)
                : postRepository.searchRecent(normalizedKeyword, pageable);
    }

    @Transactional
    public PostResponse getPost(Long postId, Long viewerMemberId) {
        if (postRepository.increaseViewCount(postId) == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다.");
        }
        Post post = getActivePost(postId);
        return toResponse(post, viewerMemberId);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long memberId, PostUpdateRequest request) {
        Post post = getActivePost(postId);
        verifyAuthor(post, memberId, "게시글 수정 권한이 없습니다.");
        validateUploadUrl(request.getAttachFileUrl());
        post.update(request.getTitle(), request.getContent(), request.getAttachFileUrl());
        return toResponse(post, memberId);
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = getActivePost(postId);
        verifyAuthor(post, memberId, "게시글 삭제 권한이 없습니다.");
        post.delete();
    }

    public Slice<PostListResponse> getPostsByCursor(
            LocalDateTime lastCreatedAt,
            Long lastPostId,
            int size
    ) {
        int safeSize = Math.max(1, Math.min(size, 50));
        return postRepository.findPostListByCursor(
                lastCreatedAt,
                lastPostId,
                PageRequest.of(0, safeSize)
        );
    }

    public Post getActivePost(Long postId) {
        return postRepository.findActiveById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다."));
    }

    private PostResponse toResponse(Post post, Long viewerMemberId) {
        long commentCount = commentRepository.countByPostPostId(post.getPostId());
        long likeCount = postLikeRepository.countByPostPostId(post.getPostId());
        boolean liked = viewerMemberId != null
                && postLikeRepository.existsByPostPostIdAndMemberMemberId(post.getPostId(), viewerMemberId);
        return PostResponse.from(post, commentCount, likeCount, liked);
    }

    private void verifyAuthor(Post post, Long memberId, String message) {
        if (!post.getMemberId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", message);
        }
    }

    private void validateUploadUrl(String url) {
        if (url != null && !url.startsWith("/uploads/")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_ATTACH_FILE", "유효하지 않은 첨부 이미지 경로입니다.");
        }
    }
}

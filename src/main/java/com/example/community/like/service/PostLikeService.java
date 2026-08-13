package com.example.community.like.service;

import com.example.community.global.ApiException;
import com.example.community.like.domain.PostLike;
import com.example.community.like.repository.PostLikeRepository;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import com.example.community.post.domain.Post;
import com.example.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;

    @Transactional
    public long like(Long postId, Long memberId) {
        if (postLikeRepository.existsByPostPostIdAndMemberMemberId(postId, memberId)) {
            throw new ApiException(HttpStatus.CONFLICT, "POST_ALREADY_LIKED", "이미 좋아요한 게시글입니다.");
        }
        Post post = postService.getActivePost(postId);
        Member member = memberRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        try {
            postLikeRepository.saveAndFlush(new PostLike(post, member));
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.CONFLICT, "POST_ALREADY_LIKED", "이미 좋아요한 게시글입니다.");
        }
        return postLikeRepository.countByPostPostId(postId);
    }

    @Transactional
    public long unlike(Long postId, Long memberId) {
        postService.getActivePost(postId);
        PostLike postLike = postLikeRepository.findByPostPostIdAndMemberMemberId(postId, memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "POST_ALREADY_UNLIKED", "이미 좋아요가 취소된 게시글입니다."));
        postLikeRepository.delete(postLike);
        postLikeRepository.flush();
        return postLikeRepository.countByPostPostId(postId);
    }
}

package com.example.community.like.repository;

import com.example.community.like.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostPostIdAndMemberMemberId(Long postId, Long memberId);

    Optional<PostLike> findByPostPostIdAndMemberMemberId(Long postId, Long memberId);

    long countByPostPostId(Long postId);

    void deleteByMemberMemberId(Long memberId);
}

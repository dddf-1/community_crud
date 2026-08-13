package com.example.community.comment.repository;

import com.example.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "member")
    List<Comment> findAllByPostPostIdOrderByCreatedAtAsc(Long postId);

    @EntityGraph(attributePaths = {"member", "post"})
    Optional<Comment> findWithMemberAndPostByCommentId(Long commentId);

    long countByPostPostId(Long postId);

    void deleteByMemberMemberId(Long memberId);
}

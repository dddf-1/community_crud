package com.example.community.comment.dto;

import com.example.community.comment.domain.Comment;
import com.example.community.member.dto.AuthorResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long id;
    private final Long postId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final AuthorResponse author;

    public CommentResponse(
            Long id,
            Long postId,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            AuthorResponse author
    ) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.author = author;
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getPostId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                AuthorResponse.from(comment.getMember())
        );
    }
}

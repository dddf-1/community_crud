package com.example.community.post.dto;

import com.example.community.member.dto.AuthorResponse;
import com.example.community.post.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private final Long id;
    private final AuthorResponse author;
    private final String title;
    private final String content;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String attachFileUrl;
    private final long commentCount;
    private final long likeCount;
    private final boolean liked;

    public PostResponse(
            Long id,
            AuthorResponse author,
            String title,
            String content,
            int viewCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String attachFileUrl,
            long commentCount,
            long likeCount,
            boolean liked
    ) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attachFileUrl = attachFileUrl;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.liked = liked;
    }

    public static PostResponse from(Post post, long commentCount, long likeCount, boolean liked) {
        return new PostResponse(
                post.getId(),
                AuthorResponse.from(post.getMember()),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getImageUrl(),
                commentCount,
                likeCount,
                liked
        );
    }
}

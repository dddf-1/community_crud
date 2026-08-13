package com.example.community.post.dto;

import com.example.community.member.dto.AuthorResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {

    private final Long id;
    private final AuthorResponse author;
    private final String title;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final String attachFileUrl;
    private final long commentCount;
    private final long likeCount;

    public PostListResponse(
            Long id,
            Long memberId,
            String nickname,
            String profileImageUrl,
            String title,
            int viewCount,
            LocalDateTime createdAt,
            String attachFileUrl,
            long commentCount,
            long likeCount
    ) {
        this.id = id;
        this.author = new AuthorResponse(memberId, nickname, profileImageUrl);
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.attachFileUrl = attachFileUrl;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
    }
}

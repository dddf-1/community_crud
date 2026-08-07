package com.example.community.post.dto;

import com.example.community.post.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {

    private final Long id;
    private final Long MemberId;
    private final String nickname;
    private final String title;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final String attachFileUrl;

    public PostListResponse(
            Long id,
            Long memberId,
            String nickname,
            String title,
            int viewCount,
            LocalDateTime createdAt,
            String attachFileUrl
    ) {
        this.id = id;
        this.MemberId = memberId;
        this.nickname = nickname;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.attachFileUrl = attachFileUrl;
    }
    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getPostId(),
                post.getMember().getMemberId(),
                post.getMember().getNickname(),
                post.getTitle(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getImageUrl()
        );
    }

}

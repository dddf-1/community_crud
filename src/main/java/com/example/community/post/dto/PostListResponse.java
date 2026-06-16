package com.example.community.post.dto;

import java.time.LocalDateTime;

public class PostListResponse {

    private final Long id;
    private final Long MemberId;
    private final String title;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final String attachFileUrl;

    public PostListResponse(
            Long id,
            Long memberId,
            String title,
            int viewCount,
            LocalDateTime createdAt,
            String attachFileUrl
    ) {
        this.id = id;
        this.MemberId = memberId;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.attachFileUrl = attachFileUrl;
    }
    public Long getId() {
        return id;
    }
    public Long getMemberId() {
        return MemberId;
    }
    public String getTitle() {
        return title;
    }
    public int getViewCount() {
        return viewCount;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getAttachFileUrl() {
        return attachFileUrl;
    }

}

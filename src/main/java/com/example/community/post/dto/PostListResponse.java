package com.example.community.post.dto;

import java.time.LocalDateTime;

public class PostListResponse {

    private final Long id;
    private final Long MemberId;
    private final String title;
    private final int viewCount;
    private final LocalDateTime createdAt;

    public PostListResponse(Long id, Long MemberId, String title, int viewCount, LocalDateTime createdAt) {
        this.id = id;
        this.MemberId = MemberId;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
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

}

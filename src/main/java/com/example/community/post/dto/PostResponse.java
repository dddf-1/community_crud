package com.example.community.post.dto;

import com.example.community.post.domain.Post;
import java.time.LocalDateTime;

public class PostResponse {

    private Long id;
    // 게시글 작성자 ID
    // 응답에 포함하면 클라이언트가 작성자 정보를 구분할 수 있다.
    private Long memberId;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String attachFileUrl;

    public PostResponse(
            Long id,
            Long memberId,
            String title,
            String content,
            int viewCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String attachFileUrl
                        ) {
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attachFileUrl = attachFileUrl;
    }

    public static PostResponse from(Post post) {

        // Post 도메인 객체를 그대로 응답하지 않고 DTO로 변환한다.
        // 이렇게 하면 내부 도메인 구조가 외부 API 응답에 직접 노출되는 것을 줄일 수 있다.
        return new PostResponse(
                post.getId(),
                post.getMemberId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getImageUrl()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getViewCount() {
        return viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getAttachFileUrl() {
        return attachFileUrl;
    }
}
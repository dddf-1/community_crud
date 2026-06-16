package com.example.community.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    // 수정할 게시글 제목
    private String title;

    // 수정할 게시글 내용
    private String content;

    private String attachFileUrl;
}
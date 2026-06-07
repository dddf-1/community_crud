package com.example.community.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    // 게시글 생성 요청에서 받을 제목
    private String title;

    // 게시글 생성 요청에서 받을 내용
    private String content;
}
package com.example.community.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    // 수정할 게시글 제목
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 26, message = "제목은 26자 이하로 입력해주세요.")
    private String title;

    // 수정할 게시글 내용
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 1500, message = "내용은 1500자 이하로 입력해주세요.")
    private String content;

    private String attachFileUrl;
}

package com.example.community.member.dto;

import com.example.community.member.domain.Member;
import lombok.Getter;

@Getter
public class AuthorResponse {

    private final Long memberId;
    private final String nickname;
    private final String profileImageUrl;

    public AuthorResponse(Long memberId, String nickname, String profileImageUrl) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static AuthorResponse from(Member member) {
        return new AuthorResponse(member.getMemberId(), member.getNickname(), member.getProfileImageUrl());
    }
}

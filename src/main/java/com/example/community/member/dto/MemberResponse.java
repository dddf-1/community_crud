package com.example.community.member.dto;

import com.example.community.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long memberId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    public MemberResponse(Long memberId, String email, String nickname, String profileImageUrl) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}

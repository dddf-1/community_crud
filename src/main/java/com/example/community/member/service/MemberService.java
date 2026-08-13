package com.example.community.member.service;

import com.example.community.global.ApiException;
import com.example.community.member.domain.Member;
import com.example.community.member.dto.MemberResponse;
import com.example.community.member.dto.MemberUpdateRequest;
import com.example.community.member.dto.PasswordUpdateRequest;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.like.repository.PostLikeRepository;
import com.example.community.post.repository.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public MemberResponse getMember(Long memberId) {
        return MemberResponse.from(getActiveMember(memberId));
    }

    public void checkEmailAvailable(String email) {
        if (email == null || email.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "이메일을 입력해주세요.");
        }
        if (memberRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "ALREADY_EXIST_EMAIL", "이미 사용 중인 이메일입니다.");
        }
    }

    public void checkNicknameAvailable(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "닉네임을 입력해주세요.");
        }
        if (memberRepository.existsByNicknameAndDeletedAtIsNull(nickname)) {
            throw new ApiException(HttpStatus.CONFLICT, "ALREADY_EXIST_NICKNAME", "이미 사용 중인 닉네임입니다.");
        }
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = getActiveMember(memberId);
        if (!member.getNickname().equals(request.getNickname())
                && memberRepository.existsByNicknameAndDeletedAtIsNull(request.getNickname())) {
            throw new ApiException(HttpStatus.CONFLICT, "ALREADY_EXIST_NICKNAME", "이미 사용 중인 닉네임입니다.");
        }
        validateUploadUrl(request.getProfileImageUrl());
        member.updateProfile(request.getNickname(), request.getProfileImageUrl());
        return MemberResponse.from(member);
    }

    @Transactional
    public void updatePassword(Long memberId, PasswordUpdateRequest request) {
        Member member = getActiveMember(memberId);
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = getActiveMember(memberId);
        commentRepository.deleteByMemberMemberId(memberId);
        postLikeRepository.deleteByMemberMemberId(memberId);
        postRepository.findAllByMemberMemberIdAndDeletedAtIsNull(memberId)
                .forEach(post -> post.delete());
        member.withdraw();
    }

    private Member getActiveMember(Long memberId) {
        return memberRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
    }

    private void validateUploadUrl(String url) {
        if (url != null && !url.startsWith("/uploads/")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_IMAGE", "유효하지 않은 프로필 이미지 경로입니다.");
        }
    }
}

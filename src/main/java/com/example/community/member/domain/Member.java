package com.example.community.member.domain;

import com.example.community.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // 회원 테이블명이 users
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 기본 생성자를 만들어주는 어노테이션
// JPA에서는 DB 조회할 때 Entity를 직접 만들어야 해서 필요함.
// protected인 이유
// --> 기본 생성자를 아무 곳에서 못쓰게 막아두겠다는 뜻.
// 즉, JPA가 Entity를 만들 수 있도록 기본 생성자는 만들어 주되,
// 외부에서 객체를 만들지 못하게 막는 목적
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 값을 자동으로 생성하여 PK 생성 -> auto_increment 기능을 사용하겠다는 의미
    // user_id를 사용자가 직접 지정하기 않기 때문에 사용
    @Column(name = "user_id")
    // DB에서 이 컬럼 이름은 user_id로 사용하겠다는 의미
    private Long memberId;

    @Column(nullable = false, unique = true, length = 255)
    // DB에서의 제약을 나타낸다.
    // nullable = false -> null값을 허용하지 않겠다.
    // unique = true -> 중복 방지
    // length = 255 -> 글자 수 제한
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    // role은 이 사용자가 어떤 권한을 가졌는지 파악하기 위해 사용했음.
    // 사용자 / 관리자로 나눴고 두 개로 나눈 이유는
    // 로그인 후 어떤 기능에 접근할 수 있는지 판단하려고 만들었음.
    // 권한이 하나만 있으면 누구나 어느 기능을 사용할 수 있다고 판단해서 이렇게 설정했음.
    // @Enumerated 는 Enum 타입을 문자열로 저장하겠다는 뜻임
    // --> 의미가 명확해지게 하기 위함

    public Member(String email, String password, String nickname, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public Member(String email, String password, String nickname, String profileImageUrl, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }
    public Long getId() {
        return memberId;
    }
}
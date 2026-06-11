package com.example.community.post.domain;

import com.example.community.global.BaseEntity;
import com.example.community.member.domain.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToMany(mappedBy = "post")
    private List<PostImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    // 다대일 관계를 설정하고, 연관 데이터를 가져오게 함.
    // @ManyToOne -> 여러 개가 하나를 가진다(post->user N:1)
    // fetch=FetchType.Lazy -> 지연로딩
    // 작성자 정보를 바로 가져오지 않고 필요할 때 가져오겠다는 의미

    // Lazy를 쓴 이유
    // 매번 작성자 전체 정보가 있어야하는 건 아니라고 생각.
    // post에서는 Lazy 설정해서 필요한 순간에만 연관 객체를 불러옴.
    @JoinColumn(name = "user_id", nullable = false)
    // posts의 외래키가 user_id 라는 것을 의미
    // 이 코드를 통해서 member 필드가 user_id 와 연결됨.

    private Member member;

    @Column(nullable = false, length = 26)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "imange_url", length = 500)
    private String imageUrl;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Post(Member member, String title, String content, String imageUrl) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.viewCount = 0;
    }

    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public Long getId() {
        return postId;
    }

    public Long getMemberId() {
        return member.getMemberId();
    }
}
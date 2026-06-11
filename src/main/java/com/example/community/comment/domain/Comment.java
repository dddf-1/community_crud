package com.example.community.comment.domain;

import com.example.community.global.BaseEntity;
import com.example.community.member.domain.Member;
import com.example.community.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 댓글이 달린 게시글
    // comments 테이블의 post_id 컬럼이 posts 테이블의 post_id를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    // @ManyToOne -> 여러 개가 하나를 가진다(Comment -> Post N:1)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 댓글 작성자
    // comments 테이블의 user_id 컬럼이 users 테이블의 user_id를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // @ManyToOne -> 여러 개가 하나를 가진다(Comment -> Member N:1)
    private Member member;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Comment(Post post, Member member, String content) {
        this.post = post;
        this.member = member;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
        // 댓글 수정 메서드
    }
}
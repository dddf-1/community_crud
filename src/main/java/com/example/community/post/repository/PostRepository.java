package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import com.example.community.post.dto.PostListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

// 게시글 DB 접근을 담당하는 JPA Repository이다.
// JpaRepository<Post, Long>를 상속하면 Spring Data JPA가 자동으로 구현체를 만들어준다.
//
// Post  → 이 Repository가 관리할 Entity 타입
// Long  → Post Entity의 기본키 타입
//
// 그래서 save(), findAll(), findById(), deleteById() 같은 기본 CRUD 메서드를
// 직접 작성하지 않아도 바로 사용할 수 있다.
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            select new com.example.community.post.dto.PostListResponse(
                p.postId,
                m.memberId,
                m.nickname,
                p.title,
                p.viewCount,
                p.createdAt,
                p.imageUrl                        
            ) 
             from Post p
             join p.member m
             order by p.createdAt desc, p.postId desc
           """)

    Slice<PostListResponse> findPostList(Pageable pageable);

    @Modifying
    @Query("""
    update Post p
    set p.viewCount = p.viewCount + 1
    where p.postId = :postId
""")
    void increaseViewCount(@Param("postId") Long postId);

    @Query("""
    select new com.example.community.post.dto.PostListResponse(
        p.postId,
        m.memberId,
        m.nickname,
        p.title,
        p.viewCount,
        p.createdAt,
        p.imageUrl
    )
    from Post p
    join p.member m
    where
        p.createdAt < :lastCreatedAt
        or (
            p.createdAt = :lastCreatedAt
            and p.postId < :lastPostId
        )
    order by p.createdAt desc, p.postId desc
""")
    Slice<PostListResponse> findPostListByCursor(
            @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
            @Param("lastPostId") Long lastPostId,
            Pageable pageable
    );
}


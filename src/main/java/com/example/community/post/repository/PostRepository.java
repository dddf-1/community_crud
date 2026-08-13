package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import com.example.community.post.dto.PostListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p join fetch p.member where p.postId = :postId and p.deletedAt is null")
    Optional<Post> findActiveById(@Param("postId") Long postId);

    @Query("""
            select new com.example.community.post.dto.PostListResponse(
                p.postId,
                m.memberId,
                m.nickname,
                m.profileImageUrl,
                p.title,
                p.viewCount,
                p.createdAt,
                p.imageUrl,
                count(distinct c.commentId),
                count(distinct pl.postLikeId)
            )
            from Post p
            join p.member m
            left join p.comments c
            left join p.likes pl
            where p.deletedAt is null
            group by p.postId, m.memberId, m.nickname, m.profileImageUrl,
                     p.title, p.viewCount, p.createdAt, p.imageUrl
            order by p.createdAt desc, p.postId desc
            """)
    Slice<PostListResponse> findPostList(Pageable pageable);

    @Query("""
            select new com.example.community.post.dto.PostListResponse(
                p.postId,
                m.memberId,
                m.nickname,
                m.profileImageUrl,
                p.title,
                p.viewCount,
                p.createdAt,
                p.imageUrl,
                count(distinct c.commentId),
                count(distinct pl.postLikeId)
            )
            from Post p
            join p.member m
            left join p.comments c
            left join p.likes pl
            where p.deletedAt is null
              and (lower(p.title) like lower(concat('%', :keyword, '%'))
                   or lower(p.content) like lower(concat('%', :keyword, '%')))
            group by p.postId, m.memberId, m.nickname, m.profileImageUrl,
                     p.title, p.viewCount, p.createdAt, p.imageUrl
            order by p.createdAt desc, p.postId desc
            """)
    Slice<PostListResponse> searchRecent(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            select new com.example.community.post.dto.PostListResponse(
                p.postId,
                m.memberId,
                m.nickname,
                m.profileImageUrl,
                p.title,
                p.viewCount,
                p.createdAt,
                p.imageUrl,
                count(distinct c.commentId),
                count(distinct pl.postLikeId)
            )
            from Post p
            join p.member m
            left join p.comments c
            left join p.likes pl
            where p.deletedAt is null
              and (lower(p.title) like lower(concat('%', :keyword, '%'))
                   or lower(p.content) like lower(concat('%', :keyword, '%')))
            group by p.postId, m.memberId, m.nickname, m.profileImageUrl,
                     p.title, p.viewCount, p.createdAt, p.imageUrl
            order by case
                       when lower(p.title) = lower(:keyword) then 0
                       when lower(p.title) like lower(concat('%', :keyword, '%')) then 1
                       else 2
                     end,
                     p.createdAt desc,
                     p.postId desc
            """)
    Slice<PostListResponse> searchRelevant(@Param("keyword") String keyword, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Post p
            set p.viewCount = p.viewCount + 1
            where p.postId = :postId and p.deletedAt is null
            """)
    int increaseViewCount(@Param("postId") Long postId);

    @Query("""
            select new com.example.community.post.dto.PostListResponse(
                p.postId,
                m.memberId,
                m.nickname,
                m.profileImageUrl,
                p.title,
                p.viewCount,
                p.createdAt,
                p.imageUrl,
                count(distinct c.commentId),
                count(distinct pl.postLikeId)
            )
            from Post p
            join p.member m
            left join p.comments c
            left join p.likes pl
            where p.deletedAt is null
              and (p.createdAt < :lastCreatedAt
                   or (p.createdAt = :lastCreatedAt and p.postId < :lastPostId))
            group by p.postId, m.memberId, m.nickname, m.profileImageUrl,
                     p.title, p.viewCount, p.createdAt, p.imageUrl
            order by p.createdAt desc, p.postId desc
            """)
    Slice<PostListResponse> findPostListByCursor(
            @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
            @Param("lastPostId") Long lastPostId,
            Pageable pageable
    );

    List<Post> findAllByMemberMemberIdAndDeletedAtIsNull(Long memberId);
}

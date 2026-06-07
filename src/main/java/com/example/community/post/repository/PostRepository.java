package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// 게시글 DB 접근을 담당하는 JPA Repository이다.
// JpaRepository<Post, Long>를 상속하면 Spring Data JPA가 자동으로 구현체를 만들어준다.
//
// Post  → 이 Repository가 관리할 Entity 타입
// Long  → Post Entity의 기본키 타입
//
// 그래서 save(), findAll(), findById(), deleteById() 같은 기본 CRUD 메서드를
// 직접 작성하지 않아도 바로 사용할 수 있다.
public interface PostRepository extends JpaRepository<Post, Long> {
}
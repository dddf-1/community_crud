package com.example.community.post.service;

import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import com.example.community.post.domain.Post;
import com.example.community.post.dto.PostCreateRequest;
import com.example.community.post.dto.PostResponse;
import com.example.community.post.dto.PostUpdateRequest;
import com.example.community.post.repository.PostRepository;
import java.util.List;

// final 필드를 가진 생성자를 Lombok이 자동으로 만들어준다.
// 그래서 postRepository, memberRepository를 직접 생성자로 주입받는 코드를 안 써도 된다.
import lombok.RequiredArgsConstructor;

// 이 클래스가 비즈니스 로직을 담당하는 Service 클래스임을 Spring에게 알려준다.
// Controller는 요청을 받고, Service는 실제 로직을 처리한다.
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    // 게시글 저장, 조회, 수정, 삭제를 담당하는 Repository이다.
    // DB에 직접 접근하는 역할은 Service가 아니라 Repository가 한다.
    private final PostRepository postRepository;

    // 회원 정보를 조회하기 위한 Repository이다.
    // 게시글 작성 시 memberId만 가지고는 JPA 연관관계를 만들 수 없기 때문에,
    // memberId로 Member 엔티티를 먼저 조회해야 한다.
    private final MemberRepository memberRepository;

    public PostResponse createPost(Long memberId, PostCreateRequest request) {

        // 1. 로그인한 사용자 ID로 실제 Member 엔티티를 조회한다.
        //
        // 예전 방식에서는 Post 안에 Long memberId를 바로 넣을 수 있었다.
        // 하지만 JPA 연관관계에서는 Post가 Member 객체를 가지고 있어야 한다.
        //
        // 그래서 memberId 숫자를 그대로 넣는 것이 아니라,
        // memberRepository.findById(memberId)를 통해 DB에서 Member를 찾아온다.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. 게시글 엔티티를 생성한다.
        //
        // 첫 번째 값으로 memberId가 아니라 Member 객체를 넣는다.
        // 이유는 Post 엔티티가 @ManyToOne 관계로 Member를 가지고 있기 때문이다.
        //
        // image 기능을 아직 구현하지 않았다면 마지막 값은 null로 둔다.
        // 나중에 파일 업로드 기능을 만들면 request.getImage() 같은 값으로 바꿀 수 있다.
        Post post = new Post(
                member,
                request.getTitle(),
                request.getContent(),
                null
        );

        // 3. 게시글을 DB에 저장한다.
        //
        // JpaRepository의 save()를 사용하면 INSERT 쿼리가 실행된다.
        // post_id는 @GeneratedValue 때문에 DB에서 자동 생성된다.
        // 그래서 예전처럼 post.setId(++sequence)를 직접 할 필요가 없다.
        Post savedPost = postRepository.save(post);

        // 4. 저장된 Post 엔티티를 응답 DTO로 변환해서 반환한다.
        //
        // Entity를 그대로 응답하면 내부 구조가 외부에 노출될 수 있다.
        // 그래서 PostResponse.from(savedPost)를 통해 필요한 값만 응답한다.
        return PostResponse.from(savedPost);
    }

    /**
     * 게시글 목록 조회 기능
     *
     * @return 전체 게시글 목록을 PostResponse 리스트로 반환한다.
     */
    public List<PostResponse> getPosts() {

        // 1. postRepository.findAll()로 DB에 저장된 모든 게시글을 조회한다.
        //
        // JpaRepository가 기본 제공하는 메서드라서 직접 SQL을 작성하지 않아도 된다.
        return postRepository.findAll()

                // 2. 조회 결과는 List<Post> 형태이다.
                // API 응답으로는 Entity를 그대로 내보내지 않고 DTO로 바꿔야 한다.
                .stream()

                // 3. 각 Post 엔티티를 PostResponse로 변환한다.
                // PostResponse.from(post)를 각각 실행하는 것과 같다.
                .map(PostResponse::from)

                // 4. 변환된 결과를 List<PostResponse>로 다시 모은다.
                .toList();
    }

    /**
     * 게시글 단건 조회 기능
     *
     * @param postId 조회할 게시글 ID
     * @return 조회된 게시글 정보를 PostResponse 형태로 반환한다.
     */
    public PostResponse getPost(Long postId) {

        // 1. postId로 게시글을 조회한다.
        //
        // findById는 Optional<Post>를 반환한다.
        // 게시글이 있을 수도 있고 없을 수도 있기 때문이다.
        Post post = postRepository.findById(postId)

                // 2. 게시글이 없으면 예외를 발생시킨다.
                // 없는 게시글을 조회하려고 했다는 의미이다.
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 3. 조회된 Post 엔티티를 응답 DTO로 변환해서 반환한다.
        return PostResponse.from(post);
    }

    /**
     * 게시글 수정 기능
     *
     * @param postId 수정할 게시글 ID
     * @param memberId 로그인한 사용자 ID
     *                 게시글 작성자와 같은 사람인지 확인하기 위해 필요하다.
     * @param request 게시글 수정 요청 DTO
     * @return 수정된 게시글 정보를 PostResponse 형태로 반환한다.
     */
    public PostResponse updatePost(Long postId, Long memberId, PostUpdateRequest request) {

        // 1. 수정하려는 게시글을 DB에서 조회한다.
        // 게시글이 없으면 수정할 수 없으므로 예외를 발생시킨다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 2. 로그인한 사용자와 게시글 작성자가 같은지 확인한다.
        //
        // post.getMemberId()는 Post 안에 있는 Member 객체에서 memberId를 꺼내는 메서드이다.
        // 즉, 이 게시글을 작성한 회원의 ID를 의미한다.
        //
        // memberId는 현재 로그인한 사용자의 ID이다.
        //
        // 둘이 다르면 남의 게시글을 수정하려는 상황이므로 막아야 한다.
        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        // 3. 게시글 내용을 수정한다.
        //
        // Entity의 필드를 Service에서 직접 setTitle, setContent로 바꾸는 것보다,
        // Post 엔티티 안에 update() 메서드를 만들어서 수정하는 편이 좋다.
        //
        // 이유는 게시글 수정 규칙을 Post 엔티티 안에 모아둘 수 있기 때문이다.
        post.update(
                request.getTitle(),
                request.getContent(),
                null
        );

        // 4. 수정된 게시글을 저장한다.
        //
        // JPA에서는 이미 조회된 엔티티의 값을 바꾸면 트랜잭션 안에서 자동으로 변경 감지가 된다.
        // 하지만 지금은 초반 단계라 save()를 한 번 더 호출해도 이해하기 쉽고 문제 없다.
        Post updatedPost = postRepository.save(post);

        // 5. 수정된 게시글을 응답 DTO로 변환해서 반환한다.
        return PostResponse.from(updatedPost);
    }

    /**
     * 게시글 삭제 기능
     *
     * @param postId 삭제할 게시글 ID
     * @param memberId 로그인한 사용자 ID
     */
    public void deletePost(Long postId, Long memberId) {

        // 1. 삭제하려는 게시글을 먼저 조회한다.
        //
        // 바로 deleteById(postId)를 해도 삭제는 가능하지만,
        // 작성자 권한 확인을 해야 하므로 먼저 Post를 조회해야 한다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 2. 로그인한 사용자와 게시글 작성자가 같은지 확인한다.
        //
        // 다른 사용자가 작성한 게시글은 삭제할 수 없어야 한다.
        // 이것이 게시글 삭제에 대한 인가 처리이다.
        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        // 3. 권한 확인이 끝난 뒤 게시글을 삭제한다.
        //
        // JpaRepository의 deleteById()를 사용하면 DELETE 쿼리가 실행된다.
        postRepository.deleteById(postId);
    }
}
package com.example.community.comment.service;

import com.example.community.comment.domain.Comment;
import com.example.community.comment.dto.CommentRequest;
import com.example.community.comment.dto.CommentResponse;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.global.ApiException;
import com.example.community.member.domain.Member;
import com.example.community.member.repository.MemberRepository;
import com.example.community.post.domain.Post;
import com.example.community.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;

    public List<CommentResponse> getComments(Long postId) {
        postService.getActivePost(postId);
        return commentRepository.findAllByPostPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse createComment(Long postId, Long memberId, CommentRequest request) {
        Post post = postService.getActivePost(postId);
        Member member = memberRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."));
        Comment comment = commentRepository.save(new Comment(post, member, request.getCommentContent().trim()));
        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse updateComment(
            Long postId,
            Long commentId,
            Long memberId,
            CommentRequest request
    ) {
        Comment comment = getComment(postId, commentId);
        verifyAuthor(comment, memberId);
        comment.update(request.getCommentContent().trim());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long memberId) {
        Comment comment = getComment(postId, commentId);
        verifyAuthor(comment, memberId);
        commentRepository.delete(comment);
    }

    private Comment getComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findWithMemberAndPostByCommentId(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."));
        if (!comment.getPostId().equals(postId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다.");
        }
        postService.getActivePost(postId);
        return comment;
    }

    private void verifyAuthor(Comment comment, Long memberId) {
        if (!comment.getMemberId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "댓글 수정 또는 삭제 권한이 없습니다.");
        }
    }
}

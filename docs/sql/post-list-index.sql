-- 게시글 목록 조회 최적화
-- 정렬: created_at DESC, post_id DESC
-- Cursor Pagination에서도 동일한 정렬 조건 사용

CREATE INDEX idx_posts_created_at_post_id
    ON posts (created_at DESC, post_id DESC);
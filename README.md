## 게시글 목록 조회 성능 개선

### 문제

기존 게시글 목록 조회는 Pageable 기반 Offset Pagination을 사용했다.

약 10,000건의 테스트 데이터를 기준으로 깊은 페이지를 조회했을 때
EXPLAIN에서 Full Table Scan 및 filesort가 확인되었다.
---

### 1차 개선 - 목록 조회 인덱스
게시글 목록 정렬 조건에 맞춰 다음 복합 인덱스를 적용했다.

```sql
CREATE INDEX idx_posts_created_at_post_id
ON posts (created_at DESC, post_id DESC);
```
### 2차 개선 - Cursor Pagination
기존 Offset Pagination과 비교하기 위해 Cursor 기반 조회 API를 추가했다.

Cursor 조건
```sql
WHERE created_at < :lastCreatedAt
   OR (
       created_at = :lastCreatedAt
       AND post_id < :lastPostId
   )
ORDER BY created_at DESC, post_id DESC
```
---

### IntelliJ에서 실행 테스트

- 기존 Offset

    ![img_2.png](img_2.png)
- Cursor

    ![img_3.png](img_3.png)

### 결과
테스트 데이터: 약 10,000건

로컬 Postman 3회 반복 측정:
- Offset: 59ms / 32ms / 36ms
- 평균: 약 42.3ms
- Cursor: 16ms / 17ms / 18ms
- 평균: 17.0ms


테스트 환경에서는 Cursor 방식의 응답 시간이 더 안정적으로 낮게 측정되었다.
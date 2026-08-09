# howareyoujake

> 동시성 문제 개선과 Kubernetes 기반 클라우드 환경에서 운영 가능한 커뮤니티 서비스 구축 프로젝트입니다.
---
- 개발 기간 : 2026-05-12 ~ ~ing(보완 작업 중)

- 개발 인원 : 본인 1명

## 📌 프로젝트 요약

https://github.com/dddf-1/community_crud.git

Community는 사용자 간 게시글과 댓글을 공유하는 커뮤니티 서비스입니다.

단순한 CRUD 구현을 넘어 실제 서비스 운영 환경을 고려하여 다음 경험을 목표로 진행했습니다.

- JWT 기반 인증/인가 구현
- 게시글 조회 성능 개선을 위한 Pagination 적용
- 조회수 증가 과정에서 발생하는 Lost Update 문제 개선
- AWS 기반 인프라 구성 및 Private Network 설계
- Kubernetes 기반 배포 환경 구축
- Monitoring & Logging 환경 구성

---

## 🛠 사용기술

### Backend
- Java
- Spring Boot
- QueryDSL
- MySQL

### Infrastructure
- AWS 
- Kubernetes

---

## 🏗 아키텍쳐

구성 흐름:

Client -> Load Balancer -> Ingress Controller -> k8s -> Spring Boot Pod -> Database

## ☸ k8s 배포 구성

서비스 배포 환경은 Kubernetes와 Helm Chart 기반으로 구성했습니다.

배포 설정은 별도의 GitOps Repository에서 관리했습니다.

구성 요소:

- Helm Chart
- Deployment
- Service
- Ingress
- ConfigMap

배포 : https://github.com/dddf-1/community-gitops.git 

---

## 🚀 주요 구현 기능

### Authentication
- 회원가입 / 로그인
- JWT 기반 Stateless 인증
- Header & Cookie 방식 인증 지원

### Community
- 게시글 CRUD
- 댓글 작성 및 조회
- 게시글 Pagination 적용

---

## 🔥 주요 개선 경험

### 1. N+1 Query 문제 개선

게시글 목록 조회 과정에서 연관 관계 데이터를 함께 조회할 때
Entity 조회 방식에서는 추가 쿼리가 반복 발생하는 문제가 발생할 수 있습니다.

해결:

- Entity 전체 조회 방식 대신 DTO Projection 적용
- 목록 조회 시 필요한 데이터만 Join하여 조회

결과:

- 불필요한 추가 Query 감소
- 조회 성능 개선
- API 응답 구조 단순화

---

### 2. 대용량 게시글 목록 조회 성능 개선

게시글 데이터가 증가할 경우 Offset 기반 Pagination은 
뒤쪽 페이지 조회 시 불필요한 데이터 탐색 비용이 증가할 수 있습니다.

개선:

- Spring Data Pageable 기반 Pagination 적용
- 목록 조회 DTO Projection 적용
- 조회 정렬 조건에 맞는 복합 Index 적용

적용 Index:

```sql
CREATE INDEX idx_posts_created_at_post_id
ON posts (created_at DESC, post_id DESC);
```

효과:

- Full Table Scan 감소
- 검색 조건 기반 조회 성능 개선

자세한 사항은 PR 확인

---

### 3. Lost Update 문제 개선

조회수 증가 요청이 동시에 발생할 경우,
여러 요청이 동일한 값을 읽고 저장하면서 데이터 유실 문제가 발생할 수 있었습니다.

개선 방향:
- 기존 조회 → 증가 → 저장 방식 개선
- DB 레벨에서 원자적 업데이트 처리
- 동시 요청 환경에서도 데이터 정합성 유지

---

### 5. 조회 성능 향상을 위한 Index 적용

게시글 목록 조회 및 정렬 조건을 고려하여
조회 빈도가 높은 컬럼에 Index를 적용했습니다.

적용 기준:

- 게시글 정렬 기준 컬럼
- Foreign Key 조회 컬럼



---

### 5. 운영 환경 구성 및 장애 경험

실제 운영 환경을 고려하여:

- Public / Private Subnet 분리
- ALB 기반 트래픽 관리
- Ingress 외부 접근 구성
- Prometheus + Grafana Monitoring
- Loki 기반 Log 수집

환경을 구축했습니다.

---

## 📝 트러블슈팅

### DNS Cache 문제 해결

Ingress/NLB 변경 이후 일부 환경에서 기존 IP로 요청되는 문제가 발생했습니다.

원인:
- Local DNS Resolver Cache

해결:
- DNS Cache 초기화 후 정상 접근 확인

---

### JWT 인증 방식 개선

문제:
- API 테스트 환경과 Frontend 요청 방식 차이 발생

해결:
- Authorization Header 방식
- Cookie 기반 Token 방식

두 가지 인증 흐름 지원

---

## 📌 후기

기존에 개발을 하거나 프로젝트를 할 때 백엔드/프론트엔드 코드를 기준으로만 진행해왔었는데
이번처럼 운영 환경에서 배포하고 이 과정에서 발생하는 문제를 고려하고 해결하는 경험을 해서 많이 배운 것 같습니다.
특히 AWS 와 k8s 기반 서비스 운영 구조를 이해하고 공부할 수 있었다는 점에서 많이 배웠습니다.

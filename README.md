# 📝 무명일기 (mmd-backend) - ver 1.0

![version](https://img.shields.io/badge/version-1.0.0-blue)
![license](https://img.shields.io/badge/license-MIT-green.svg)
![java](https://img.shields.io/badge/java-17-blueviolet)
![spring](https://img.shields.io/badge/spring--boot-3.x-brightgreen)

무명일기는 사용자가 이름 없이 감정을 기록하고, AI에게 다듬기를 요청하거나, 댓글과 반응을 통해 익명 소통이 가능한 감정 기반 일기 서비스입니다.

---

## 🚀 주요 기능

### ✅ 인증
- 이메일 기반 Magic Link 로그인
- JWT 기반 인증 및 사용자 식별

### 📒 일기 (Diary)
- 일기 작성 / 단일 조회 / 수정 / 삭제
- 공개 여부, 댓글 허용 여부 설정
- 본인 일기만 수정/삭제 가능

### 👀 일기 뷰 (Diary View)
- 최근자 일기 조회(무한 스크롤)
- 사용자가 본 일기 기록 관리
- 북마크 기능 제공

### 💬 댓글 (Comment)
- 특정 일기에 대한 댓글 작성 / 조회 / 삭제
- 본인 댓글만 삭제 가능

### ❤️ 반응 (Reaction)
- 일기에 좋아요 / 공감 등의 반응 추가 (중복 불가)
- 사용자-일기-반응 타입 단일 조합만 허용

### ✨ AI 다듬기 (Refine)
- Gemini 기반 Vertex AI 사용
- 사용자가 작성한 일기를 톤/목적별로 AI가 다듬어 줌
- 하루 5회 다듬기 제한
- 사용자가 다듬은 결과를 일기에 반영 여부 선택 가능

### 🚨 신고 (Report)
- 댓글 및 일기 신고 기능 제공 예정

---

## 🛠️ 기술 스택

| 분야 | 사용 기술 |
|------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Build Tool | Gradle |
| DB | PostgreSQL |
| Security | Spring Security + JWT |
| Mail | JavaMailSender (Naver SMTP) |
| AI | Vertex AI Gemini (Google Cloud) |
| Test | SpringBootTest, MockMvc, JUnit5 |

---

## ⚙️ 설정 방법

### 1. 환경 변수 설정 (.env.properties)

루트 또는 `src/main/resources`에 다음 환경 변수를 설정

```properties
DB_URL=jdbc:postgresql://localhost:5432/db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

MAIL_USERNAME=your_email@naver.com
MAIL_PASSWORD=your_email_password

GCP_PATH=classpath:gcp/your-key.json
GCP_PROJECT_ID=your-gcp-project
GCP_REGION=us-central1
GEMINI_MODEL=gemini-2.0-flash
```

### 2. Google Cloud Vertex AI 설정

* 서비스 계정 JSON 키를 `src/main/resources/gcp/`에 위치
* `GCP_PATH` 환경변수에 키 경로 설정
* 프로젝트 ID, Region, 모델 이름 설정

---

## 📚 참고 문서

* [Spring AI + Gemini 사용해보기](https://velog.io/@sin_0/Spring-Ai-Gemini-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0)
* [Spring AI 공식 문서](https://spring.io/projects/spring-ai)
* [Gemini API 공식 문서](https://docs.spring.io/spring-ai/reference/api/chat/vertexai-gemini-chat.html)

---

## 🪐 배포

* **Render**를 이용한 백엔드 자동 CI/CD
* **Neon**을 이용한 PostgreSQL 클라우드 DB 연동
* 환경변수는 Render/Neon 환경에 맞춰 별도 관리

---

## 📜 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 [LICENSE](./LICENSE) 파일을 확인하세요.


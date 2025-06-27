# 📝 무명일기 (mmd-backend) - ver 1.0

![version](https://img.shields.io/badge/version-1.0.0-blue)
![license](https://img.shields.io/badge/license-MIT-green.svg)
![java](https://img.shields.io/badge/java-17-blueviolet)
![spring](https://img.shields.io/badge/spring--boot-3.x-brightgreen)

무명일기는 사용자가 이름 없이 감정을 기록하고, AI에게 다듬기를 요청하거나, 댓글 및 반응을 통해 익명 소통이 가능한 감정 기반 일기 서비스입니다.

---

## 🚀 주요 기능

### ✅ 인증
- 이메일 기반 Magic Link 로그인 방식
- JWT 기반 인증 및 사용자 식별

### 📒 일기
- 일기 작성 / 조회 / 수정 / 삭제
- 공개 여부, 댓글 허용 여부 설정
- 본인 일기만 수정 가능

### 💬 댓글
- 특정 일기에 댓글 작성 / 조회 / 수정 / 삭제
- 본인 댓글만 수정 가능

### ❤️ 반응
- 일기에 좋아요 / 공감 등의 반응 추가 (중복 불가)
- 사용자-일기-반응 타입 단일 조합만 허용

### ✨ AI 다듬기 (Gemini 기반)
- 사용자가 쓴 일기를 AI가 정제해줌 (톤에 따라 선택 가능)
- 하루 5회 제한

### 🚨 신고
- 댓글 / 일기 신고 기능 제공 예정

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

### 1. 환경 변수 (.env.properties)

```properties
DB_URL=jdbc:postgresql://localhost:5432/mmdb
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

MAIL_USERNAME=your_email@naver.com
MAIL_PASSWORD=your_email_password

GCP_PATH=classpath:gcp/your-key.json
GCP_PROJECT_ID=your-gcp-project
GCP_REGION=us-central1
GEMINI_MODEL=gemini-2.0-flash
````

---

## 📚 참고 문서

* [Spring AI + Gemini 사용해보기](https://velog.io/@sin_0/Spring-Ai-Gemini-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0)
* [Spring AI 공식 문서](https://spring.io/projects/spring-ai)
* [Gemini API 공식 문서](https://docs.spring.io/spring-ai/reference/api/chat/vertexai-gemini-chat.html)

---

## 📜 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. [LICENSE](./LICENSE) 파일을 참고하세요.


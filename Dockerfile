# 빌드 스테이지 - dependencies 레이어 캐시
FROM gradle:8.8.0-jdk17 AS build
WORKDIR /workspace

# gradle wrapper 및 빌드 파일 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# dependencies 캐싱을 위한 의존성 다운로드
RUN --mount=type=cache,target=/root/.gradle \
    gradle build --no-daemon -x test

# 소스 복사 및 빌드
COPY . .

RUN --mount=type=cache,target=/root/.gradle \
    gradle bootJar --no-daemon -x test

# 런타임
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

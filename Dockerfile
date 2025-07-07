# 빌드 스테이지 - dependencies 레이어 캐시
FROM gradle:8.8.0-jdk17 AS build
WORKDIR /workspace

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

RUN --mount=type=cache,target=/root/.gradle \
    gradle dependencies --no-daemon

# 빌드 스테이지 - 소스 복사 및 빌드
COPY . .

RUN --mount=type=cache,target=/root/.gradle \
    gradle bootJar -x test --no-daemon

# 런타임
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

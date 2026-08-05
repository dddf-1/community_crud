# 1단계: Spring Boot 애플리케이션 빌드
FROM gradle:8.10.2-jdk17 AS builder

WORKDIR /app

# 의존성 관련 파일을 먼저 복사하여 Docker 캐시 활용
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 실행 가능한 JAR 생성
COPY src ./src

RUN ./gradlew clean bootJar --no-daemon && \
    JAR_FILE=$(find build/libs -name "*.jar" ! -name "*-plain.jar" | head -n 1) && \
    cp "$JAR_FILE" /app/app.jar


# 2단계: 실행에 필요한 Java 환경만 포함
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# C:\Users\code\Documents\project\yoogiscloset\Dockerfile
# ----------------------------------------
# 1. BUILDER STAGE: 애플리케이션 빌드
# Java 17을 사용하며, Gradle 빌드를 위해 eclipse-temurin:17-jdk 사용
# ----------------------------------------
  FROM eclipse-temurin:17-jdk AS builder

  # 작업 디렉토리 설정
  WORKDIR /app
  
  # Gradle Wrapper, 설정 파일, 의존성 파일 복사 (캐싱 최적화)
  COPY gradlew .
  COPY gradle gradle
  COPY build.gradle .
  COPY settings.gradle .
  
  # 의존성 다운로드
  RUN ./gradlew dependencies --console=plain
  
  # 나머지 소스 코드 복사
  COPY src src
  
  # 애플리케이션 빌드 및 JAR 파일 생성
  RUN ./gradlew bootJar
  
  # ----------------------------------------
  # 2. RUNTIME STAGE: 애플리케이션 실행
  # 실행을 위해 더 가벼운 eclipse-temurin:17-jre 사용
  # ----------------------------------------
  FROM eclipse-temurin:17-jre
  
  # 작업 디렉토리 설정
  WORKDIR /app
  
  # 빌더 스테이지에서 생성된 JAR 파일을 복사하고 app.jar로 이름 통일
  COPY --from=builder /app/build/libs/*.jar /app/app.jar
  
  # Spring Boot 서버 포트 (8080) 노출
  EXPOSE 8080
  
  # 컨테이너 시작 시 실행될 명령어 정의
  ENTRYPOINT ["java", "-jar", "app.jar"]
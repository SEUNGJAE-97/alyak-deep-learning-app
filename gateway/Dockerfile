FROM gradle:8.5-jdk17 AS builder
WORKDIR /build

COPY build.gradle.kts settings.gradle.kts /build/
RUN gradle build -x test --parallel --continue || true

COPY . /build
RUN gradle build -x test --parallel

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=builder /build/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
# Use Temurin OpenJDK 17 as base image for build stage
FROM eclipse-temurin:17-jdk-jammy AS builder

# Install dos2unix to fix line ending issues
RUN apt-get update && apt-get install -y dos2unix && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files first
COPY gradlew ./
COPY gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY gradle.properties ./

# Fix line endings for gradlew
RUN dos2unix gradlew

# Make gradlew executable
RUN chmod +x gradlew

# Verify gradlew exists and is executable
RUN ls -la gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew build -x test --no-daemon

# Create a new stage for runtime
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 
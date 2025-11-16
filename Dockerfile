# ===== STAGE 1: Build stage =====
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install dependencies for build
RUN apk add --no-cache curl

WORKDIR /app

# 1️⃣ Copy Gradle wrapper and config files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Make gradlew executable
RUN chmod +x gradlew

# 2️⃣ Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# 3️⃣ Copy source code and build
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# Verify JAR was created
RUN ls -lh build/libs/

# ===== STAGE 2: Runtime stage =====
FROM eclipse-temurin:21-jre-alpine

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership
RUN chown app:app app.jar

# Switch to non-root user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

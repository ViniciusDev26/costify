# Costify Production Dockerfile
# Multi-stage build for optimal image size and security

# Stage 1: Build Stage
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for dependency caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for production build)
# Tests should be run in CI/CD pipeline before building Docker image
RUN ./mvnw clean package -DskipTests -B -Pprod

# Stage 2: Runtime Stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Add metadata labels
LABEL maintainer="Costify Team"
LABEL description="Costify - Recipe Cost Calculator"
LABEL version="0.0.1"

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -S costify && adduser -S costify -G costify

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R costify:costify /app

# Switch to non-root user
USER costify

# Expose application port
EXPOSE 8080

# Health check endpoint (uses SERVER_PORT env var, defaults to 8080)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT:-8080}/api/actuator/health || exit 1

# JVM configuration for production
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom"

# Spring profile (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=prod

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

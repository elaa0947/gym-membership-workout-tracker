# ==========================================
# Multi-stage Dockerfile for Cloud Platforms
# Compatible with Render, Railway, Fly.io, AWS
# ==========================================

# Stage 1: Build JAR using Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom.xml and cache dependencies
COPY backend/gym-tracker-backend/pom.xml .
RUN mvn -B dependency:go-offline || true

# Copy source code and build production jar
COPY backend/gym-tracker-backend/src ./src
RUN mvn -B -DskipTests clean package

# Stage 2: Minimal, secure runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Run as non-privileged system user
RUN groupadd -r spring && useradd -r -g spring spring \
    && mkdir -p /app/data \
    && chown -R spring:spring /app

USER spring:spring

COPY --from=build --chown=spring:spring /workspace/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-8080} -jar app.jar"]

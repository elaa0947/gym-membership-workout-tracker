# Stage 1: Build JAR with Maven
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Copy maven wrapper and pom.xml
COPY backend/gym-tracker-backend/mvnw .
COPY backend/gym-tracker-backend/.mvn .mvn
COPY backend/gym-tracker-backend/pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build production jar
COPY backend/gym-tracker-backend/src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Run as non-privileged system user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

COPY --from=build /workspace/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=${PORT}", "-jar", "app.jar"]

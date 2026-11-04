FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S gym && adduser -S gym -G gym
COPY --from=build /workspace/target/gym-tracker-backend-0.0.1-SNAPSHOT.jar app.jar
USER gym
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]

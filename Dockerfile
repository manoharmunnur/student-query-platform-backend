# 1) Build stage: use Maven + JDK to build the jar
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom and source code
COPY pom.xml .
COPY src ./src

# Build the application (skip tests to speed up)
RUN mvn clean package -DskipTests

# 2) Run stage: use smaller JRE image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the first stage
COPY --from=build /app/target/sqp-backend-0.0.1-SNAPSHOT.jar app.jar

# App will listen on this port
EXPOSE 8080

# Use PORT env var if provided (Render, etc.)
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]

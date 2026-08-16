# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Add non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy jar from builder
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

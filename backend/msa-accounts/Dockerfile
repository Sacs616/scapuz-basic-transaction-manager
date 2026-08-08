# Build stage
FROM eclipse-temurin:21-jdk-jammy AS builder

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy

# Install curl for health checks
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r spring && \
    useradd -r -g spring spring

USER spring
WORKDIR /app

# Copy the JAR file (not extracted layers)
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
# Use standard JAR execution
ENTRYPOINT ["java", "-jar", "app.jar"]
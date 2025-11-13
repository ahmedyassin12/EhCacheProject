# Stage 1: Build the JAR
FROM gradle:7-jdk17 AS builder
WORKDIR /app
COPY . .
# Cache Gradle dependencies (faster rebuilds)
RUN gradle clean build -x test --no-daemon

# Stage 2: Run the app
 # Smaller image
FROM eclipse-temurin:17-jdk as build
WORKDIR /app

# Use wildcard to handle version changes (e.g., 0.0.1-SNAPSHOT → 0.0.2-SNAPSHOT)
COPY --from=builder /app/build/libs/ehCache-*.jar /app/ehCache.jar

EXPOSE 9090

# Run as non-root user for security
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

CMD ["java", "-jar", "ehCache.jar"]
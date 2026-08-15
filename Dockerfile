# ------------------------------------------------------------------------------
# Root Dockerfile for Render / Cloud Deployments (Spring Boot Backend)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper & project descriptor
COPY backend/mvnw backend/pom.xml ./
COPY backend/.mvn ./.mvn

# Ensure execution permissions on the wrapper script
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B || true

# Copy backend source and compile
COPY backend/src ./src
RUN ./mvnw clean package -DskipTests

# ------------------------------------------------------------------------------
# Production JRE Runtime
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/aspirantos-backend-*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

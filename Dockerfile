# Build the JAR
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN ./gradlew :app:bootJar --no-daemon

# Run the JAR
FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
RUN useradd -u 10001 -m gitforge \
    && mkdir -p /data/repos \
    && chown gitforge /data/repos
COPY --from=build /src/app/build/libs/GitForge.jar /app/GitForge.jar
USER 10001
ENV GITFORGE_STORAGE_ROOT=/data/repos
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -fs http://localhost:8081/actuator/health/liveness || exit 1
ENTRYPOINT ["java", "-jar", "/app/GitForge.jar"]
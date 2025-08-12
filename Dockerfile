# Multi-stage build for SysML v2 API Services
FROM openjdk:11-jdk-slim as builder

# Install sbt
RUN apt-get update && \
    apt-get install -y curl wget && \
    wget https://github.com/sbt/sbt/releases/download/v1.8.2/sbt-1.8.2.tgz && \
    tar -xzf sbt-1.8.2.tgz && \
    mv sbt /usr/local/ && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt && \
    rm sbt-1.8.2.tgz && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy build files
COPY build.sbt .
COPY project/ project/

# Download dependencies (for better caching)
RUN sbt update

# Copy source code
COPY . .

# Replace persistence.xml with Docker version
RUN cp conf/META-INF/persistence-docker.xml conf/META-INF/persistence.xml

# Build the application
RUN sbt clean compile stage

# Production stage
FROM openjdk:11-jre-slim

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built application from builder stage
COPY --from=builder /app/target/universal/stage/ .

# Change ownership to app user
RUN chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Expose port
EXPOSE 9000

# Set environment variables
ENV JAVA_OPTS="-Xmx1024m -Xms512m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9000/ || exit 1

# Start the application with Docker configuration and database system properties from environment variables
CMD ["sh", "-c", "./bin/sysml-v2-api-services -Dconfig.file=conf/application-docker.conf -Ddb.url=jdbc:postgresql://${DB_HOST:-postgres}:${DB_PORT:-5432}/${DB_NAME:-sysml2}?sslmode=require -Ddb.user=${DB_USER:-postgres} -Ddb.password=${DB_PASSWORD:-mysecretpassword}"]
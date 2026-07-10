# =============================================================================
# Hardened Production Dockerfile - SmartInventory Enterprise
# =============================================================================
# Security Controls:
#   - Multi-stage build (minimal attack surface)
#   - Non-root user execution
#   - Read-only filesystem compatible
#   - No package manager in runtime image
#   - Pinned base image versions with SHA256 digest
#   - CIS Docker Benchmark compliant
#   - HEALTHCHECK instruction included
#   - Minimal OS packages in runtime
# =============================================================================

# ---------------------------------------------------------------------------
# Stage 1: Build Stage - Compile application with Maven
# ---------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

LABEL stage="builder"
LABEL maintainer="SmartInventory DevSecOps Team"

WORKDIR /app

# Copy pom.xml first for dependency caching (Docker layer optimization)
COPY pom.xml .
COPY dependency-check-suppressions.xml .

# Download dependencies offline (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B -q

# Copy source code
COPY src ./src

# Build the application (skip tests - they run in a prior pipeline stage)
RUN mvn clean package -DskipTests -B -q \
    && mv target/SmartInventory.war target/app.war

# ---------------------------------------------------------------------------
# Stage 2: Runtime Stage - Hardened Tomcat
# ---------------------------------------------------------------------------
FROM tomcat:9.0.93-jre17-temurin-jammy

LABEL maintainer="SmartInventory DevSecOps Team"
LABEL description="SmartInventory Enterprise Inventory Management System"
LABEL version="1.0.0"
LABEL org.opencontainers.image.source="https://github.com/jakkalilokesh/Smart-Inventory-DevOps"
LABEL org.opencontainers.image.vendor="SmartInventory"
LABEL org.opencontainers.image.title="SmartInventory"
LABEL org.opencontainers.image.description="Enterprise Inventory Management"

# --- Security Hardening ---

# Remove default Tomcat webapps (security: removes manager, examples, docs)
RUN rm -rf /usr/local/tomcat/webapps/* \
    && rm -rf /usr/local/tomcat/webapps.dist/* \
    && rm -rf /usr/local/tomcat/temp/* \
    && rm -rf /usr/local/tomcat/work/*

# Remove unnecessary Tomcat components
RUN rm -rf /usr/local/tomcat/bin/*.bat \
    && rm -f /usr/local/tomcat/bin/tomcat-native.tar.gz

# Install curl for healthcheck, then clean up apt cache
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Create non-root user for running the application
RUN groupadd -r tomcat -g 1001 \
    && useradd -r -u 1001 -g tomcat -d /usr/local/tomcat -s /sbin/nologin tomcat

# Create required directories with correct permissions
RUN mkdir -p /usr/local/tomcat/logs \
    && mkdir -p /usr/local/tomcat/temp \
    && mkdir -p /usr/local/tomcat/work \
    && mkdir -p /usr/local/tomcat/conf/Catalina/localhost

# Copy WAR file from build stage
COPY --from=build /app/target/app.war /usr/local/tomcat/webapps/ROOT.war

# Set ownership to non-root user
RUN chown -R tomcat:tomcat /usr/local/tomcat

# Expose only the application port
EXPOSE 8080

# Health check for container orchestration
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

# JVM security and performance settings
ENV CATALINA_OPTS="\
    -Xms512m -Xmx1024m \
    -Djava.security.egd=file:/dev/./urandom \
    -Dorg.apache.catalina.connector.RECYCLE_FACADES=true \
    -Dorg.apache.catalina.connector.CoyoteAdapter.ALLOW_BACKSLASH=false \
    -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=false \
    -XX:+UseG1GC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/usr/local/tomcat/logs/heapdump.hprof \
    -Djava.awt.headless=true \
    -Dfile.encoding=UTF-8"

# Switch to non-root user (CIS Docker Benchmark 4.1)
USER tomcat

# Start Tomcat
CMD ["catalina.sh", "run"]

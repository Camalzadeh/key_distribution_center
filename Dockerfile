# =============================================================================
# Key Distribution Center — production image
#
# Built by CI and pulled by whatever runs it; nothing is compiled at deploy
# time. Two stages, so Maven and the JDK never ship.
# =============================================================================

# --- stage 1: build ----------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Dependencies resolve in their own layer, keyed only on pom.xml. Editing a
# .java file then rebuilds in seconds instead of re-downloading the world.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package \
 && mv target/key-distribution-center-*.jar /build/app.jar

# --- stage 2: run ------------------------------------------------------------
# Alpine for two reasons: the image is about 100 MB smaller, and busybox
# brings wget, so the healthcheck below needs nothing installed. The
# application is pure Java — JPA, Thymeleaf and the Postgres JDBC driver —
# so musl versus glibc does not arise.
FROM eclipse-temurin:17-jre-alpine

# A non-root user with no shell and no home to write into. The base image runs
# as root, which for a process that only ever reads its own jar is a
# capability with no purpose.
RUN addgroup -S -g 10001 kdc \
 && adduser -S -u 10001 -G kdc -H -s /sbin/nologin kdc

WORKDIR /app
COPY --from=build --chown=root:root /build/app.jar /app/app.jar

USER 10001:10001

# 8080 is the application. The actuator port (8081) is deliberately NOT
# exposed and is bound to 127.0.0.1 inside the container: the healthcheck
# below reaches it, and nothing else can.
EXPOSE 8080

# MaxRAMPercentage, not -Xmx: the JVM then tracks whatever memory limit the
# container is given instead of a number baked in here. The default of 25 %
# wastes most of a small limit.
#
# SerialGC is the right collector at this size — G1's own bookkeeping threads
# cost more than they return on one or two cores with a heap this small.
#
# ExitOnOutOfMemoryError matters for a supervised container: without it a JVM
# that has exhausted its heap keeps the port open and keeps failing requests,
# so the restart policy never fires and the healthcheck is the only thing that
# notices.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.awt.headless=true"

# A real probe, not a liveness fiction: the actuator health endpoint checks out
# a pooled connection, so UP means the database answered. `grep UP` is the
# assertion — a 200 carrying {"status":"DOWN"} must not read as healthy.
#
# start-period covers JVM start plus Hibernate's schema pass on a cold
# database; the container is not marked unhealthy while that is still running.
HEALTHCHECK --interval=30s --timeout=5s --start-period=75s --retries=3 \
  CMD wget -q -O- http://127.0.0.1:8081/actuator/health 2>/dev/null | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

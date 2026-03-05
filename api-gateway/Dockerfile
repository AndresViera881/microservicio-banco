# ---------- STAGE 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copiar TODO el proyecto raíz
COPY . .

# Construir SOLO el módulo api-gateway
RUN mvn clean package -pl api-gateway -am -DskipTests


# ---------- STAGE 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no root
RUN addgroup -S bancogroup && adduser -S bancouser -G bancogroup

# Copiar jar generado del módulo correcto
COPY --from=builder /build/api-gateway/target/*.jar app.jar

RUN chown bancouser:bancogroup app.jar

USER bancouser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
# ---------- STAGE 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copiar TODO el proyecto raíz (porque usamos context: .)
COPY . .

# Construir SOLO el módulo movimiento-service
RUN mvn clean package -pl movimiento-service -am -DskipTests


# ---------- STAGE 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no root
RUN addgroup -S bancogroup && adduser -S bancouser -G bancogroup

# Copiar el jar generado desde el módulo específico
COPY --from=builder /build/movimiento-service/target/*.jar app.jar

RUN chown bancouser:bancogroup app.jar

USER bancouser

EXPOSE 8083

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
CMD wget -qO- http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]
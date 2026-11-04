# ── Etapa 1: Build con Maven y JDK 21 ──
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar pom.xml y descargar dependencias para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar el JAR omitiendo tests
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Etapa 2: Runtime con JRE 21 y usuario no root ──
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario y grupo no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR generado desde la etapa de construcción con permisos para appuser
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]


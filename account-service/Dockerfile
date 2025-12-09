# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle.properties* ./

# Copiar módulos compartidos
COPY common-lib ./common-lib
COPY shared-repositories ./shared-repositories

# Copiar código del microservicio
COPY account-service ./account-service

# Construir el proyecto
RUN gradle :account-service:bootJar --no-daemon

# Stage 2: Runtime
FROM amazoncorretto:17-alpine
WORKDIR /app

# Crear usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR desde stage de build
COPY --from=build /app/account-service/build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8082

# Variables de entorno
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/api/accounts || exit 1

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

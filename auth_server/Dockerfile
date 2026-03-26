# Etapa 1: Build con Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con JRE ligero
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Debug: JDWP agent habilitado via variable de entorno (se activa si DEBUG=true)
ENV JAVA_TOOL_OPTIONS=""
ENTRYPOINT ["java", "-jar", "app.jar"]
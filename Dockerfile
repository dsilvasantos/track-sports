# Etapa 1: Build com Maven
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem Final com JDK
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/live-sports-tracker-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

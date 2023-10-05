# Этап 1: сборка приложения
FROM maven:3.8.3-openjdk-17-slim AS build

WORKDIR /app

COPY  pom.xml .
COPY  src ./src

RUN mvn package

# Этап 2: запуск приложения
FROM openjdk:17-jdk-slim-buster

WORKDIR /app

COPY --from=build /app/target/sber_practice-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
# Сборка
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Копируем файлы проекта
COPY pom.xml .
COPY src ./src
# Собираем JAR
RUN mvn clean package -DskipTests

# Запуск
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Копируем собранный JAR
COPY --from=build /app/target/*.jar app.jar
# Открываем порт 8080
EXPOSE 8080
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
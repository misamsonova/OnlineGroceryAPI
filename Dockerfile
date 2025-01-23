# Используем образ с OpenJDK 17
FROM openjdk:17-slim AS builder

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven

# Копируем проект в контейнер
COPY . /app

# Устанавливаем рабочую директорию
WORKDIR /app

# Собираем проект с Maven
RUN mvn clean package -DskipTests

# Открываем порт для приложения
EXPOSE 9090

# Команда для запуска приложения
CMD ["java", "-jar", "target/OnlineGroceryAPI-1.0-SNAPSHOT.jar"]


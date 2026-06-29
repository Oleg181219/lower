# ===== Stage 1: Build =====
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Копируем pom.xml для кэширования зависимостей
COPY pom.xml .

# Скачиваем зависимости (кэшируется, если pom.xml не меняется)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение (пропускаем тесты и генерацию jOOQ для скорости)
RUN mvn clean package -DskipTests -Dskip.jooq.generation=true -B

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем часовой пояс (Москва)
ENV TZ=Europe/Moscow
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Копируем jar файл из builder stage
COPY --from=builder /app/target/*.jar app.jar

# Меняем владельца на spring пользователя
RUN chown spring:spring app.jar

# Переключаемся на non-root пользователя
USER spring:spring

# Переменные окружения
ENV JAVA_OPTS="-Xmx512m -Xms256m -Duser.timezone=Europe/Moscow"
ENV SPRING_PROFILES_ACTIVE=production

# Открываем порт (по умолчанию 8080)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
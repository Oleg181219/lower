# ===== Stage 1: Build =====
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -Dskip.jooq.generation=true -B

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

ENV TZ=Europe/Moscow
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Копируем JAR
COPY --from=builder /app/target/*.jar app.jar

# ✅ ВАЖНО: Копируем шрифты ОТДЕЛЬНО из builder stage
COPY --from=builder /app/src/main/resources/fonts /app/fonts

RUN chown -R spring:spring /app

USER spring:spring

# ✅ Передаем путь к шрифтам через переменную окружения
ENV JAVA_OPTS="-Xmx512m -Xms256m -Duser.timezone=Europe/Moscow -Dapp.fonts.dir=/app/fonts"
ENV SPRING_PROFILES_ACTIVE=production

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
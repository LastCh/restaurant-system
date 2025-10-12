# ===== STAGE 1: Build stage =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 1️⃣ Скопируем только Gradle файлы для кеша зависимостей
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

# Делаем wrapper исполняемым
RUN chmod +x gradlew

# 2️⃣ Кешируем зависимости (Gradle скачает их заранее)
RUN ./gradlew dependencies --no-daemon || true

# 3️⃣ Теперь копируем исходники и собираем приложение
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# ===== STAGE 2: Runtime stage =====
FROM eclipse-temurin:21-jre

# 4️⃣ Создаем "не-root" пользователя
RUN addgroup --system app && adduser --system --ingroup app app

WORKDIR /app

# 5️⃣ Копируем собранный JAR
COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown app:app app.jar

USER app

# 6️⃣ Документируем порт и добавляем healthcheck
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 7️⃣ Запуск
ENTRYPOINT ["java", "-jar", "app.jar"]

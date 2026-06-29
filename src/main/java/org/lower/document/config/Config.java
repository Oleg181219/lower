package org.lower.document.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.TimeZone;

@Configuration
public class Config {

    /**
     * Устанавливает часовой пояс JVM в Europe/Moscow при старте приложения.
     * Это гарантирует, что все вызовы new Date(), Calendar.getInstance(),
     * LocalDateTime.now() и т.д. будут использовать московское время.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));

        // Дополнительно: фиксируем ZoneId для Java 8+ Date/Time API
        // (на случай, если где-то используется ZoneId.systemDefault())
        System.setProperty("user.timezone", "Europe/Moscow");
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрируем модуль для Java 8 Date/Time API
        mapper.registerModule(new JavaTimeModule());

        // Устанавливаем часовой пояс
        TimeZone moscowTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        mapper.setTimeZone(moscowTimeZone);

        // Отключаем сериализацию дат как timestamp (число миллисекунд)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}

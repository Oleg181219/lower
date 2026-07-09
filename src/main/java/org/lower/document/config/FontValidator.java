package org.lower.document.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class FontValidator implements ApplicationRunner {

    private static final String FONT_REGULAR_PATH = "fonts/PTSerif-Regular.ttf";
    private static final String FONT_BOLD_PATH = "fonts/PTSerif-Bold.ttf";

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== ПРОВЕРКА ШРИФТОВ ===");
        validateFont(FONT_REGULAR_PATH);
        validateFont(FONT_BOLD_PATH);
    }

    private void validateFont(String path) {
        // Используем ТОТ ЖЕ способ, что и в генераторе
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);

        if (is != null) {
            try {
                is.close();
                log.info("✅ Шрифт найден: {}", path);
            } catch (Exception e) {
                log.warn("⚠️ Шрифт найден, но ошибка при закрытии: {}", path);
            }
        } else {
            log.error("❌ КРИТИЧЕСКАЯ ОШИБКА: Шрифт {} НЕ НАЙДЕН!", path);
            throw new IllegalStateException("Критический шрифт не найден: " + path);
        }
    }
}
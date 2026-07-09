package org.lower.document.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class FontValidator implements ApplicationRunner {

    @Value("${app.fonts.dir:}")
    private String fontsDir;

    @Override
    public void run(ApplicationArguments args) {
        validateFont("PTSerif-Regular.ttf");
        validateFont("PTSerif-Bold.ttf");
    }

    private void validateFont(String fileName) {
        // Проверка в файловой системе
        if (fontsDir != null && !fontsDir.isEmpty()) {
            java.io.File fontFile = new java.io.File(fontsDir, fileName);
            if (fontFile.exists()) {
                log.info("✅ Шрифт найден в файловой системе: {} ({} байт)",
                        fontFile.getAbsolutePath(), fontFile.length());
                return;
            }
        }

        // Проверка в classpath
        InputStream is = getClass().getClassLoader().getResourceAsStream("fonts/" + fileName);
        if (is != null) {
            try {
                log.info("✅ Шрифт найден в classpath: fonts/{}", fileName);
                is.close();
            } catch (IOException e) {
                log.warn("Ошибка при проверке шрифта: {}", e.getMessage());
            }
            return;
        }

        log.error("❌ КРИТИЧЕСКАЯ ОШИБКА: Шрифт {} НЕ НАЙДЕН!", fileName);
        throw new IllegalStateException("Критический шрифт не найден: " + fileName);
    }
}
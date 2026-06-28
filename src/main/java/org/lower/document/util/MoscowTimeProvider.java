package org.lower.document.util;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;

@Component
public class MoscowTimeProvider {

    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");

    /**
     * Текущая дата в МСК
     */
    public LocalDate today() {
        return LocalDate.now(MOSCOW_ZONE);
    }

    /**
     * Текущее время в МСК
     */
    public LocalDateTime now() {
        return LocalDateTime.now(MOSCOW_ZONE);
    }

    /**
     * Текущий момент времени с привязкой к МСК
     */
    public ZonedDateTime nowZoned() {
        return ZonedDateTime.now(MOSCOW_ZONE);
    }

    /**
     * Текущий Instant (всегда UTC, но удобно для БД)
     */
    public Instant nowInstant() {
        return Instant.now();
    }

    /**
     * Текущая дата-время для legacy кода
     */
    public Date nowDate() {
        return Date.from(nowInstant());
    }
}
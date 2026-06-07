package org.lower.document.services.tg.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.config.properties.TelegramBotProperties;
import org.lower.document.dto.TelegramAuthData;
import org.lower.document.dto.WebAppUser;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramWebAppParser {

    private final ObjectMapper objectMapper;
    private final TelegramBotProperties botConfig;

    /**
     * Валидирует initDataRaw и парсит ее в объект TelegramAuthData.
     *
     * @param initData Сырая строка, полученная от window.Telegram.WebApp.initDataRaw
     * @return Объект TelegramAuthData, если валидация прошла успешно.
     * @throws SecurityException если хэш неверный.
     */
    public TelegramAuthData parseAndValidate(String initData) {
//        log.info("Starting validation for raw initDataRaw string...");

        // 1. Разбираем строку на ключ-значение
        Map<String, String> params = Arrays.stream(initData.split("&"))
                .map(param -> param.split("=", 2))
                .filter(p -> p.length == 2)
                .collect(Collectors.toMap(arr -> decode(arr[0]), arr -> decode(arr[1]), (a, b) -> a));

        String receivedHash = params.get("hash");
        if (receivedHash == null) {
            throw new SecurityException("Hash is missing from initDataRaw");
        }

        // 2. Формируем строку для проверки, как требует документация Telegram
        String dataCheckString = params.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("hash"))
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));

        // 3. Проверяем подпись
        if (!isValid(dataCheckString, receivedHash, botConfig.token())) {
            throw new SecurityException("Invalid Telegram hash signature");
        }
//        log.info("Telegram hash is valid!");

        // 4. Валидация прошла. Теперь создаем наш DTO из проверенных данных.
        try {
            WebAppUser user = objectMapper.readValue(params.get("user"), WebAppUser.class);
            return new TelegramAuthData(
                    params.get("query_id"),
                    user,
                    null, // Можете добавить парсинг 'chat' здесь, если нужно
                    Long.parseLong(params.get("auth_date")),
                    null,
                    receivedHash
            );
        } catch (Exception e) {
            log.error("Failed to parse user data from validated initDataRaw string", e);
            throw new IllegalArgumentException("Invalid data structure in initDataRaw", e);
        }
    }

    private boolean isValid(String dataCheckString, String receivedHash, String token) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            byte[] secret = hmac.doFinal(token.getBytes(StandardCharsets.UTF_8));

            hmac = Mac.getInstance("HmacSHA256");
            secretKey = new SecretKeySpec(secret, "HmacSHA256");
            hmac.init(secretKey);
            byte[] calculatedHashBytes = hmac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            String calculatedHashHex = bytesToHex(calculatedHashBytes);

            if (!calculatedHashHex.equals(receivedHash)) {
                log.warn("Telegram hash mismatch! \n--- dataCheckString:\n{}\n--- calculatedHash: {}\n--- receivedHash:   {}",
                        dataCheckString, calculatedHashHex, receivedHash);
                return false;
            }
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC algorithm setup failed", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    private String decode(String value) {
        return UriUtils.decode(value, StandardCharsets.UTF_8);
    }
}
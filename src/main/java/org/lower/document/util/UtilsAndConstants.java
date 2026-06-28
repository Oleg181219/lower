package org.lower.document.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class UtilsAndConstants {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String ADMIN = "Admin";
    public static final String COMPLETE = "complete";
    public static final String EMPTY_CLIENTS = "Clients is empty";
    public static final String USER_EXIST = "User exist";
    public static final String CLIENT_EXIST = "Client exist";
    public static final String WRONG_FORMAT = "wrong format";

    /**
     * Сокращает ФИО до формата "Фамилия И.И."
     * Пример: "Иванову Ивану Ивановичу" → "Иванову И.И."
     * "Петров Петр" → "Петров П."
     * "Сидоров" → "Сидоров"
     */
    public static String toShortName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return fullName;
        }
        String[] parts = fullName.trim().split("\\s+");
        return Arrays.stream(parts)
                .limit(1)  // Фамилия целиком
                .findFirst()
                .map(family -> {
                    String initials = Arrays.stream(parts)
                            .skip(1)  // Пропускаем фамилию
                            .map(word -> word.charAt(0) + ".")
                            .collect(Collectors.joining(" "));
                    return initials.isEmpty() ? family : family + " " + initials;
                })
                .orElse(fullName);
    }
}

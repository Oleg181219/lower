package org.lower.document.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lower.document.dto.enums.PersonGender;

/**
 * Данные клиента и дела, приходят с фронта.
 */
public record FullNameFormsRequest(
        // ФИО в именительном падеже — исходные данные для склонения
        @Valid
        @NotNull
        String lastName,      // Подрезов
        @Valid
        @NotNull
        String firstName,     // Александр
        @Valid
        @NotNull
        String middleName,    // Александрович
        // пол: MALE / FEMALE; может быть null — сервис сам определит по отчеству
        @Valid
        @NotNull
        PersonGender gender
) {
}
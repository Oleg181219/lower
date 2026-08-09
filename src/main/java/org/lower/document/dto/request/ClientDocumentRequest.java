package org.lower.document.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lower.document.dto.enums.PersonGender;

import java.time.LocalDate;

/**
 * Данные клиента и дела, приходят с фронта.
 */
public record ClientDocumentRequest(
// дата возбуждения дела о банкротстве (необязательно;
        // если null — в СУД/ОСФР/Росимуществе используются значения-фолбэки из шаблонов)
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate caseInitiationDate,
        // дата дела: 06.06.2023
        @Valid
        @JsonFormat(pattern = "dd.MM.yyyy")
        @NotNull
        LocalDate caseDate,

        // номер дела: №А53-10291/2023
        @Valid
        @NotNull
        String caseNumber,

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

        // дата рождения клиента: 07.09.1990
        @JsonFormat(pattern = "dd.MM.yyyy")
        @Valid
        @NotNull
        LocalDate birthDate,

        // место рождения клиента
        @Valid
        @NotNull
        String birthPlace,    // Ростовская область, г. Новочеркасск

        @Valid
        @NotNull
        String inn,           // 615018201246
        @Valid
        @NotNull
        String snils,         // 163-409-915 71

        // проживание
        @Valid
        @NotNull
        String city,          // г. Новочеркасск
        @Valid
        @NotNull
        String postalCode,    // 346400
        @Valid
        @NotNull
        String region,        // Ростовская область
        String district,      // район проживания, может быть null
        @Valid
        @NotNull
        String address,       // сп. Красный, д. 6

        // сведения о зп пенсии: 2019
        @Valid
        @NotNull
        String pensionInfo,

        // пол: MALE / FEMALE; может быть null — сервис сам определит по отчеству
        @Valid
        @NotNull
        PersonGender gender,

        // === Готовые формы ФИО (приходят от фронта после вызова /api/client/full-name-forms) ===
        @Valid
        @NotNull
        String genitive,       // "Подрезова Ивана Петровича"

        @Valid
        @NotNull
        String instrumental,   // "Подрезовым Иваном Петровичем"

        @Valid
        @NotNull
        String shortName       // "Подрезов И.П."

) {
}
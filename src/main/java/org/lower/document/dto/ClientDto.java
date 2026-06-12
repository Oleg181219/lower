package org.lower.document.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ClientDto(
        UUID id,
        UUID ownerId,
        String fullName,
        String fullNameShort,         // ФИО с инициалами: "Подрезов А.А."
        LocalDate birthDate,         // Дата рождения: "07.09.1990"
        String birthPlace,            // Место рождения: "Ростовская область, г. Новочеркасск"
        String inn,                   // ИНН: "615018201246"
        String snils,                 // СНИЛС: "163-409-915 71"
        String address
) {


}

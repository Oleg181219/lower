package org.lower.document.dto;

import org.lower.document.dto.enums.PersonGender;

/**
 * Полный результат склонения ФИО.
 * middleName может быть null, если отчество не передавалось.
 */
public record FullNameDeclensionDto(
        DeclinedNameDto lastName,
        DeclinedNameDto firstName,
        DeclinedNameDto middleName,
        PersonGender detectedGender
) {
}

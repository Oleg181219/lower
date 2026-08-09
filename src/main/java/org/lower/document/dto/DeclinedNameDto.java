package org.lower.document.dto;

/**
 * Просклонённая часть ФИО + уверенность результата.
 */
public record DeclinedNameDto(
        CaseFormsDto cases,
        String confidence   // HIGH / MEDIUM / LOW
) {
}

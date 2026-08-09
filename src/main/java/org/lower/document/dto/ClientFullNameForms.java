package org.lower.document.dto;

/**
 * Производные формы ФИО для шаблона документа.
 */
public record ClientFullNameForms(
        String genitive,       // Подрезова Александра Александровича
        String instrumental,   // Подрезовым Александром Александровичем
        String shortName       // Подрезов А.А.
) {
}
package org.lower.document.dto;

/**
 * Все 6 падежей одной части ФИО.
 */
public record CaseFormsDto(
        String nominative,
        String genitive,
        String dative,
        String accusative,
        String instrumental,
        String prepositional
) {
}

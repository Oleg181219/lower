package org.lower.document.dto;

/**
 * Данные для генерации документа ФССП
 */
public record FsspDocumentData(
        TrusteeData trustee,
        RecipientData recipient,
        DebtorData debtor,
        CourtDecisionData courtDecision,
        String procedure
) {}

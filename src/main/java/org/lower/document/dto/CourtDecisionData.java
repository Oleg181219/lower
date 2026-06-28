package org.lower.document.dto;

public record CourtDecisionData(
        String courtName,          // "Арбитражного суда Ростовской области"
        String decisionDate,       // "06.06.2023"
        String caseNumber          // "А53-10291/2023"
) {}

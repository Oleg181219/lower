package org.lower.document.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record CourtDecisionsResponse(
        UUID id,
        String courtName,
        LocalDate decisionDate,
        String caseNumber
) {
}

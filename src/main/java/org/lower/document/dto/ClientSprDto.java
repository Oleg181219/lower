package org.lower.document.dto;

import org.jooq.impl.QOM;

import java.util.UUID;

public record ClientSprDto(
        UUID id,
        UUID ownerId,
        String fullName,
        String fullNameShort,
        Integer region
) {
}

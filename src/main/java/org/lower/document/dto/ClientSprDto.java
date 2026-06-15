package org.lower.document.dto;

import java.util.UUID;

public record ClientSprDto(
        UUID id,
        UUID ownerId,
        String fullName,
        String fullNameShort
) {
}

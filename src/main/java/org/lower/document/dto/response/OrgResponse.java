package org.lower.document.dto.response;

import java.util.UUID;

public record OrgResponse(
        UUID id,
        String orgName,
        String orgAddr
) {
}

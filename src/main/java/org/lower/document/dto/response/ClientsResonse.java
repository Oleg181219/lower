package org.lower.document.dto.response;

import org.lower.document.dto.ClientSprDto;

import java.util.List;

public record ClientsResonse(
        String result,
        List<ClientSprDto> clientSprDto
) {
}

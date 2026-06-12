package org.lower.document.dto.response;

import org.lower.document.dto.ClientDto;

public record ClientResponce(
        String result,
        ClientDto client
) {
}

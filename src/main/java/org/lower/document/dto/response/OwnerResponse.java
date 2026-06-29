package org.lower.document.dto.response;

import lombok.Data;
import org.lower.document.dto.OwnerDto;

@Data
public class OwnerResponse {
    String result;
    OwnerDto owner;
}

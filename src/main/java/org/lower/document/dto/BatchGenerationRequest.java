package org.lower.document.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BatchGenerationRequest {

    private UUID clientId;
    private UUID lawyerId;
    private LocalDate requestDate;
    private List<String> documentTypes;
}
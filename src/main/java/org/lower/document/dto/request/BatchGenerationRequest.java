package org.lower.document.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BatchGenerationRequest {
    private UUID clientId;
    private List<UUID> documentsIds;
    private UUID courtDecisions;
}
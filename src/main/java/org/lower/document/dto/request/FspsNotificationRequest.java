package org.lower.document.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lower.document.dto.CourtDecision;
import org.lower.document.dto.Trustee;

import java.util.List;

@Data
public class FspsNotificationRequest {
    @JsonProperty("courtDecision")
    private CourtDecision courtDecision;

    @JsonProperty("debtor")
    private ClientRequest clientRequest;

    @JsonProperty("trustee")
    private Trustee trustee;

    @JsonProperty("procedure")
    private String procedure;

    @JsonProperty("recipientAddress")
    private String recipientAddress; // адрес отдела судебных приставов

    @JsonProperty("attachments")
    private List<String> attachments; // напр. ["Решение суда", "Паспорт должника", "Паспорт АУ"]
}

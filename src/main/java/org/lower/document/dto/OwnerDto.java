package org.lower.document.dto;

import org.lower.document.jooq.codegen.tables.records.OwnersRecord;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OwnerDto(
        UUID id,
        UUID userId,
        String fullName,
        String fullNameShort,
        String mailAddress,
        String email,
        String sroName,
        String sroInn,
        String sroOgrn,
        String sroAddress,
        String userInn,
        String userSnils,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static OwnerDto fromRecord(OwnersRecord record) {
        return new OwnerDto(
                record.getId(),
                record.getUserId(),
                record.getFullName(),
                record.getFullNameShort(),
                record.getMailAddress(),
                record.getEmail(),
                record.getSroName(),
                record.getSroInn(),
                record.getSroOgrn(),
                record.getSroAddress(),
                record.getUserInn(),
                record.getUserSnils(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}

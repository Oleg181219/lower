package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.dto.ClientDto;
import org.lower.document.dto.ClientSprDto;
import org.lower.document.dto.request.ClientRequest;
import org.lower.document.jooq.codegen.enums.RegionEnum;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.lower.document.jooq.codegen.tables.Clients.CLIENTS;
import static org.lower.document.util.UtilsAndConstants.toShortName;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ClientDao {
    private final DSLContext dsl;

    public List<ClientSprDto> getClientsByOwnerId(UUID ownerId) {
        return dsl.selectFrom(CLIENTS)
                .where(CLIENTS.OWNER_ID.eq(ownerId))
                .fetchInto(ClientsRecord.class)
                .stream()
                .filter(Objects::nonNull)
                .map(r -> new ClientSprDto(r.getId(),
                        r.getOwnerId(),
                        r.getFullName(),
                        r.getFullNameShort()))
                .toList();
    }

    public ClientsRecord getClientByUuid(UUID clientId) {
        return dsl.selectFrom(CLIENTS)
                .where(CLIENTS.ID.eq(clientId))
                .fetchOne();
    }

    public ClientDto createClient(ClientRequest clientRequest, UUID ownerId) {
        try {
            ClientsRecord record = dsl.insertInto(CLIENTS)
                    .set(CLIENTS.FULL_NAME, clientRequest.getFullName())
                    .set(CLIENTS.FULL_NAME_SHORT, toShortName(clientRequest.getFullName()))
                    .set(CLIENTS.FULL_NAME_GENITIVE, clientRequest.getFullNameGenitive())
                    .set(CLIENTS.FULL_NAME_SHORT_GENITIVE, toShortName(clientRequest.getFullNameGenitive()))
                    .set(CLIENTS.OWNER_ID, ownerId)
                    .set(CLIENTS.BIRTH_DATE, clientRequest.getBirthDate())
                    .set(CLIENTS.BIRTH_PLACE, clientRequest.getBirthPlace())
                    .set(CLIENTS.INN, clientRequest.getInn())
                    .set(CLIENTS.SNILS, clientRequest.getSnils())
                    .set(CLIENTS.ADDRESS, clientRequest.getAddress())
                    .set(CLIENTS.REGION, RegionEnum.valueOf(clientRequest.getRegion()))
                    .returning()
                    .fetchOne();
            return record != null ? fromRecord(record) : null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static ClientDto fromRecord(ClientsRecord record) {
        return new ClientDto(
                record.getId(),
                record.getOwnerId(),
                record.getFullName(),
                record.getFullNameShort(),
                record.getBirthDate(),
                record.getBirthPlace(),
                record.getInn(),
                record.getSnils(),
                record.getAddress(),
                record.getRegion()
        );
    }
}

package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static org.lower.document.jooq.codegen.tables.Clients.CLIENTS;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ClientDao {
    private final DSLContext dsl;

    public void insert() {

    }

    public ClientsRecord getClientByUuid(UUID clientId) {
        return dsl.selectFrom(CLIENTS)
                .where(CLIENTS.ID.eq(clientId))
                .fetchOne();
    }

    public ClientsRecord createClient(String fullName, String inn, String snils, String address) {
        dsl.insertInto(CLIENTS)
                .set(CLIENTS.FULL_NAME, fullName)
                .set(CLIENTS.INN, inn)
                .set(CLIENTS.SNILS, snils)
                .set(CLIENTS.ADDRESS, address)
                .onConflict()
                .doNothing()
                .execute();

        return dsl.selectFrom(CLIENTS)
                .where(CLIENTS.ID.eq(UUID.randomUUID()))
                .fetchOne();
    }
}

package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.springframework.stereotype.Component;

import javax.swing.text.Document;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientDao {
    private final DSLContext dsl;
    public void insert() {

    }

    public ClientsRecord getClientByUuid(UUID clientId) {
        return null;
    }
}

package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.lower.document.jooq.codegen.tables.records.DocumentsRecord;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.lower.document.jooq.codegen.tables.Documents.DOCUMENTS;


@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentDao{
    private final DSLContext dsl;
    public DocumentsRecord insertDocument(UUID userId, UUID clientId, String templateType,
                                          JSONB metadata, byte[] content, Integer sizeKb) {
        return dsl.insertInto(DOCUMENTS)
                .set(DOCUMENTS.USER_ID, userId)
                .set(DOCUMENTS.CLIENT_ID, clientId)
                .set(DOCUMENTS.TEMPLATE_TYPE, templateType)
                .set(DOCUMENTS.METADATA, metadata)
                .set(DOCUMENTS.CONTENT, content)
                .set(DOCUMENTS.SIZE_KB, sizeKb)
                .returning()
                .fetchOne();
    }
}

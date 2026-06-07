package org.lower.document.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.dto.RecipientInfoDto;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipientLookupService {
    private final DSLContext dsl;

    public RecipientInfoDto getRecipient(String docType, ClientsRecord client) {
      /*  var record = dsl.selectFrom(RECIPIENT_ORGS)
                .where(RECIPIENT_ORGS.DOC_TYPE.eq(docType.toLowerCase()))
                .fetchOne();

        if (record == null) {
            log.error("Recipient org not found for docType: {}", docType);
            throw new IllegalArgumentException("Орган не найден для типа: " + docType);
        }

        return new RecipientInfoDto(
                record.getOrgName(),
                record.getOrgAddress(),
                record.getOrgNote()
        );*/
        return null;
    }
}

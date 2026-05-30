package org.lower.document.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.RecipientInfoDto;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipientLookupService {
    public RecipientInfoDto getRecipient(String docType, ClientsRecord client) {

        return null;
    }
}

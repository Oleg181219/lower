package org.lower.document.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.lower.document.dao.ClientDao;
import org.lower.document.dto.ClientSprDto;
import org.lower.document.dto.response.ClientsResonse;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.lower.document.util.UtilsAndConstants.COMPLETE;
import static org.lower.document.util.UtilsAndConstants.EMPTY_CLIENTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientDao clientDao;
    private final UtilService utilService;

    public ClientsResonse getClients() {
        OwnersRecord ownersRecord = utilService.getOwnersRecord();
        UUID ownerId = ownersRecord.getId();
        ClientsResonse response;
        List<ClientSprDto> clients = clientDao.getClientsByOwnerId(ownerId);
        if (CollectionUtils.isNotEmpty(clients)) {
            response = new ClientsResonse(COMPLETE, clients);
        } else {
            response = new ClientsResonse(EMPTY_CLIENTS, null);
        }
        return response;
    }
}

package org.lower.document.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.lower.document.dao.ClientDao;
import org.lower.document.dao.OrgDao;
import org.lower.document.dto.ClientSprDto;
import org.lower.document.dto.response.ClientsResonse;
import org.lower.document.dto.response.OrgResponse;
import org.lower.document.jooq.codegen.tables.records.ClientsRecord;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.lower.document.util.UtilsAndConstants.COMPLETE;
import static org.lower.document.util.UtilsAndConstants.EMPTY_CLIENTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrgService {
    private final OrgDao orgDao;
    private final UtilService utilService;

    public List<OrgResponse> getOrganizations(String region) {
        return orgDao.getActiveOrganizationsByRegion(region);
    }

}

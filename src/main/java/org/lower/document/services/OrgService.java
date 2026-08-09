package org.lower.document.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dao.OrgDao;
import org.lower.document.dto.response.OrgResponse;
import org.springframework.stereotype.Service;

import java.util.List;

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

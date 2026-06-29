package org.lower.document.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.lower.document.dao.CourtDecisionDao;
import org.lower.document.dto.response.CourtDecisionsResponse;
import org.lower.document.jooq.codegen.tables.records.CourtDecisionsRecord;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourtDecisionsService {
    private final CourtDecisionDao courtDecisionDao;
    private final UtilService utilService;

    public List<CourtDecisionsResponse> getCourtDecisions(UUID clientId) {
        OwnersRecord owner = utilService.getOwnersRecord();
        List<CourtDecisionsRecord> courtDecisionsRecord =
                courtDecisionDao.findAllByClientIdAnOwnerId(clientId, owner.getId());
        if (CollectionUtils.isNotEmpty(courtDecisionsRecord)) {
            return courtDecisionsRecord.stream()
                    .map(this::toResponse)
                    .toList();
        } else {
            return List.of();
        }
    }

    private CourtDecisionsResponse toResponse(CourtDecisionsRecord courtDecisionsRecord) {
        return new CourtDecisionsResponse(courtDecisionsRecord.getId(),
                courtDecisionsRecord.getCourtName(), courtDecisionsRecord.getDecisionDate(),
                courtDecisionsRecord.getCaseNumber());
    }

}

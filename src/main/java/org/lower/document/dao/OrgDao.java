package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.dto.response.OrgResponse;
import org.lower.document.jooq.codegen.enums.RegionEnum;
import org.lower.document.jooq.codegen.tables.records.CourtOrgsRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.lower.document.jooq.codegen.tables.CourtOrgs.COURT_ORGS;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrgDao {
    private final DSLContext dsl;

    public List<OrgResponse> getActiveOrganizationsByRegion(String region) {
        return dsl.selectFrom(COURT_ORGS)
                .where(COURT_ORGS.IS_ACTIVE.isTrue())
                .and(COURT_ORGS.REGION_CODE.equalIgnoreCase(region))
                .fetchInto(CourtOrgsRecord.class)
                .stream()
                .map(r -> new OrgResponse(r.getId(), r.getOrgName(), r.getOrgAddress()))
                .toList();
    }

    public List<CourtOrgsRecord> getActiveOrganizationsRecByRegion(RegionEnum region) {
        return dsl.selectFrom(COURT_ORGS)
                .where(COURT_ORGS.IS_ACTIVE.isTrue())
                .and(COURT_ORGS.REGION_CODE.equalIgnoreCase(region.getLiteral()))
                .fetchInto(CourtOrgsRecord.class);

    }
}

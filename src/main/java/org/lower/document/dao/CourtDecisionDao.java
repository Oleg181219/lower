package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.lower.document.jooq.codegen.tables.records.CourtDecisionsRecord;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.lower.document.jooq.codegen.tables.CourtDecisions.COURT_DECISIONS;

@Repository
@RequiredArgsConstructor
public class CourtDecisionDao {

    private final DSLContext dsl;


    public CourtDecisionsRecord findByClientIdAnOwnerId(UUID clientId, UUID ownerId) {
        return dsl.selectFrom(COURT_DECISIONS)
                .where(COURT_DECISIONS.CLIENT_ID.eq(clientId))
                .and(COURT_DECISIONS.OWNER_ID.eq(ownerId))
                .fetchOne();
    }

    public List<CourtDecisionsRecord> findAllByClientIdAnOwnerId(UUID clientId, UUID ownerId) {
        return dsl.selectFrom(COURT_DECISIONS)
                .where(COURT_DECISIONS.CLIENT_ID.eq(clientId))
                .and(COURT_DECISIONS.OWNER_ID.eq(ownerId))
                .fetchInto(CourtDecisionsRecord.class);
    }

    public CourtDecisionsRecord findByClientId(UUID clientId) {
        return dsl.selectFrom(COURT_DECISIONS)
                .where(COURT_DECISIONS.CLIENT_ID.eq(clientId))
                .fetchOne();
    }

    public List<CourtDecisionsRecord> findByOwnerId(UUID ownerId) {
        return dsl.selectFrom(COURT_DECISIONS)
                .where(COURT_DECISIONS.OWNER_ID.eq(ownerId))
                .fetch();
    }

    public void save(String courtName, LocalDate decisionDate, String caseNumber,
                     UUID clientId, UUID ownerId) {
        dsl.insertInto(COURT_DECISIONS)
                .set(COURT_DECISIONS.OWNER_ID, ownerId)
                .set(COURT_DECISIONS.CLIENT_ID, clientId)
                .set(COURT_DECISIONS.COURT_NAME, courtName)
                .set(COURT_DECISIONS.DECISION_DATE, decisionDate)
                .set(COURT_DECISIONS.CASE_NUMBER, caseNumber)
                .execute();
    }
}
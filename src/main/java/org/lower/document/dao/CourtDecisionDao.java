package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.lower.document.jooq.codegen.tables.records.CourtDecisionsRecord;
import org.springframework.stereotype.Repository;

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

    public CourtDecisionsRecord save(CourtDecisionsRecord record) {
        return dsl.insertInto(COURT_DECISIONS)
                .set(record)
                .returning()
                .fetchOne();
    }
}
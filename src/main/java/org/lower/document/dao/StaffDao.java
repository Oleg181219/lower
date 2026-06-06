package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.dto.request.StaffRequest;
import org.lower.document.jooq.codegen.tables.records.StaffsRecord;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static org.lower.document.jooq.codegen.tables.Staffs.STAFFS;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StaffDao {
    private final DSLContext dsl;

    public StaffsRecord createStaff(StaffRequest request, UUID ownerId, UUID userId) {
        return dsl.insertInto(STAFFS)
                .set(STAFFS.USER_ID, userId)
                .set(STAFFS.OWNER_ID, ownerId) // Привязка к владельцу!
                .set(STAFFS.FULL_NAME, request.getFullName())
                .set(STAFFS.FULL_NAME_SHORT, request.getFullNameShort())
                .set(STAFFS.EMAIL, request.getEmail())
                .set(STAFFS.USER_INN, request.getInn())
                .set(STAFFS.USER_SNILS, request.getSnils())
                .returning()
                .fetchOne();
    }
}

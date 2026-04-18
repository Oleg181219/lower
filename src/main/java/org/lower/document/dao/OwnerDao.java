package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.dto.OwnerRequest;
import org.lower.document.jooq.codegen.tables.records.OwnersRecord;
import org.lower.document.jooq.codegen.tables.records.UsersRecord;
import org.springframework.stereotype.Component;

import static org.lower.document.jooq.codegen.Tables.OWNERS;

@Slf4j
@Component
@RequiredArgsConstructor
public class OwnerDao {
    private final DSLContext dsl;

    public OwnersRecord findByUsername(String email){
       return dsl.selectFrom(OWNERS)
                .where(OWNERS.EMAIL.eq(email))
               .fetchOne();
    }

    public void createNewOwner(OwnerRequest ownerRequest, UsersRecord newUser) {
        dsl.insertInto(OWNERS)
                .set(OWNERS.EMAIL, ownerRequest.getEmail())
                .set(OWNERS.FULL_NAME, ownerRequest.getFullName())
                .set(OWNERS.FULL_NAME_SHORT, ownerRequest.getFullNameShort())
                .set(OWNERS.MAIL_ADDRESS, ownerRequest.getMailAddress())
                .set(OWNERS.USER_INN, ownerRequest.getInn())
                .set(OWNERS.USER_SNILS, ownerRequest.getSnils())
                .set(OWNERS.SRO_NAME, ownerRequest.getSroName())
                .set(OWNERS.SRO_ADDRESS, ownerRequest.getSroAddress())
                .set(OWNERS.SRO_INN, ownerRequest.getSroInn())
                .set(OWNERS.SRO_OGRN, ownerRequest.getSroOgrn())
                .set(OWNERS.USER_ID, newUser.getId())
                .onConflict()
                .doNothing()
                .execute();
    }
}

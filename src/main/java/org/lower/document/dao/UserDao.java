package org.lower.document.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.lower.document.auth.JwtTokenProvider;
import org.lower.document.dto.request.OwnerRequest;
import org.lower.document.jooq.codegen.enums.RoleEnum;
import org.lower.document.jooq.codegen.tables.records.UsersRecord;
import org.lower.document.security.PasswordService;
import org.springframework.stereotype.Component;

import static org.lower.document.jooq.codegen.Tables.USERS;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDao {

    private final DSLContext dsl;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordService passwordService;

    public UsersRecord findByUsername(String username) {
        return dsl.selectFrom(USERS)
                .where(USERS.USERNAME.eq(username))
                .fetchOne();
    }

    public UsersRecord createUser(OwnerRequest ownerRequest, RoleEnum roleEnum) {
        try {
            return dsl.insertInto(USERS)
                    .set(USERS.USERNAME, ownerRequest.getEmail())
                    .set(USERS.ROLE, roleEnum)
                    .set(USERS.PASSWORD, passwordService.hashPassword(ownerRequest.getPassword()))
                    .returning()
                    .fetchOne();
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
            return null;
        }
    }
}

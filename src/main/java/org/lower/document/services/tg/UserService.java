package org.lower.document.services.tg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jooq.DSLContext;
import org.lower.document.dto.TelegramAuthData;
import org.lower.document.dto.UserShort;
import org.lower.document.dto.WebAppUser;
import org.lower.document.services.tg.parser.TelegramWebAppParser;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final DSLContext dsl;
    private final TelegramWebAppParser parser;


    /**
     * Авторизация/вход:
     * - валидирует initDataRaw и парсит TelegramAuthData
     * Возвращает короткий профиль.
     */
    @Transactional
    public UserShort initOrLoginAuthOnly(String initDataRaw, Long referrerTelegramId) {
        TelegramAuthData auth = parser.parseAndValidate(initDataRaw);
        WebAppUser tgmUser = auth.user();

        Long tgId = tgmUser.id();

//        var existing = dsl.selectFrom(USERS)
//                .where(USERS.TELEGRAM_ID.eq(tgId))
//                .fetchOne();
//
//        if (existing == null) {
//            Long refTgId = resolveReferrerTelegramId(tgId, referrerTelegramId);
//
//            try {
//                int inserted = dsl.insertInto(USERS)
//                        .set(USERS.TELEGRAM_ID, tgId)
//                        .set(USERS.LOCALE, localeEnum)
//                        .set(USERS.REFERRER_ID, refTgId)
//                        .execute();
//
//            } catch (DataIntegrityViolationException ignored) {
//                log.warn("⚠️ Пользователь {} уже существует (race)", tgId);
//            }
//
//        } else {
//
//        }
//
//        // единая точка возврата
//        var usersRecord = dsl.selectFrom(USERS)
//                .where(USERS.TELEGRAM_ID.eq(tgId))
//                .fetchOne();
//
//        boolean isAdmin = dsl.selectFrom(PIZZA_ADMIN)
//                .where(PIZZA_ADMIN.TELEGRAM_ID.eq(tgId))
//                .limit(1)
//                .fetchOptional()
//                .isPresent();
//
//        return new UserShort(
//                usersRecord.getTelegramId(),
//                usersRecord.getLocale().getLiteral(),
//                usersRecord.getReferrerId(),
//                isAdmin
//        );
        return null;
    }

}
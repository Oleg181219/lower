package org.lower.document.services.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.response.AuthResponse;
import org.lower.document.services.tg.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class TgAuthService {
    public static final String REDIS_SESSION_KEY = "session:";
    private final UserService userService;                 // Только авторизация + создание юзера


    /**
     * Обработка AUTH_INIT — вход / авто-регистрация по initData
     */
    public ResponseEntity<AuthResponse> authenticate() {
//        AuthReq authReq = request.getAuthReq();
//        String requestId = request.getRequestId();
//
//        // Проверка на null
//        if (authReq == null || ObjectUtils.isEmpty(authReq.getInitData())) {
//            return Response.WsResp.error(AUTH_INIT, requestId, "initData is required");
//        }
//
//        String rawData = authReq.getInitData();
//        Long referralCode = parseReferral(authReq.getReferralCode());
//
//        try {
//            // Создаём / авторизуем пользователя по initData
//            UserShort user = userService.initOrLoginAuthOnly(rawData, referralCode);
//
//            // ⚙️ Вытаскиваем IP / UA из Handshake
//            String ip = (String) session.getAttributes().getOrDefault("clientIp", "unknown");
//            String ua = (String) session.getAttributes().getOrDefault("userAgent", "unknown");
//
//            // Создаём сессию в Redis
//            UserSession userSession = sessions.create(user.telegramId(), ip, ua);
//
//            // Записываем атрибуты в WS-сессию
//            session.getAttributes().put("authed", true);
//            session.getAttributes().put(WsAttributes.telegramId.name(), user.telegramId());
//            session.getAttributes().put(WsAttributes.sessionId.name(), userSession.sessionId());
//            session.getAttributes().put(WsAttributes.authTime.name(), Instant.now());
//
//            redisTemplate.opsForValue().set(REDIS_SESSION_KEY + user.telegramId(), userSession.sessionId(), Duration.ofHours(1));
//            // Возвращаем Response
//            return Response.WsResp.ok(
//                    AUTH_INIT,
//                    requestId,
//                    AuthResponse.builder()
//                            .sessionId(userSession.sessionId())
//                            .sessionExpiresAt(userSession.absoluteExpiresAt())
//                            .user(user)
//                            .jettonBoxReceived(jettonRepo.checkJettonBoxComplete(user.telegramId()).isPresent())
//                            .build()
//            );
//
//        } catch (SecurityException se) {
//            log.warn("❌ Invalid initData: {}", se.getMessage());
//            return Response.WsResp.error(AUTH_INIT, requestId, "INVALID_INIT: " + se.getMessage());
//        } catch (Exception e) {
//            log.error("🔥 Internal error in AUTH_INIT", e);
//            return Response.WsResp.error(AUTH_INIT, requestId, "INTERNAL_ERROR");
//        }
        return null;
    }


    /**
     * Парсинг referral-а из строки или long
     */
    private Long parseReferral(String referralCode) {
        try {
            return referralCode != null ? Long.parseLong(referralCode) : null;
        } catch (NumberFormatException e) {
            log.warn("Invalid referralCode: {}", referralCode);
            return null;
        }
    }

}
package org.lower.document.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.*;
import org.lower.document.services.auth.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static org.lower.document.util.UtilsAndConstants.AUTHORIZATION;
import static org.lower.document.util.UtilsAndConstants.BEARER;

;

@Slf4j
@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Авторизация. Запрос с всплывающего окна при открытии страницы.
     *
     * @param request
     * @return
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> auth(@RequestBody AuthRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok().header(AUTHORIZATION, BEARER + token).build();
    }

    /**
     * Создание владельца. Нужно добавить защиту. Что бы левые не могли создаваться.
     *
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/owner")
    public ResponseEntity<OwnerResponse> owner(@RequestBody OwnerRequest request,
                                               @RequestHeader Map<String, String> headers) {
        return ResponseEntity.ok().body(authService.createOwner(request, headers));

    }

    /**
     * Создание сотрудника. Создается только владельцем.
     *
     * @param request
     * @return
     */
    @PostMapping("/register/staff")
    public ResponseEntity<?> staff(@RequestBody StaffRequest request,
                                   @RequestHeader Map<String, String> headers) {
        var token = authService.createStaff(request, headers);
        return ResponseEntity.ok().header(AUTHORIZATION, BEARER + token).build();
    }

    /* /**
     * Создание клиента. Создается только владельцем или сотрудником.
     *
     * @param request
     * @return
     *//*
    @PostMapping("/register/client")
    public ResponseEntity<?> client(@RequestBody ClientRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok().header(AUTHORIZATION, BEARER + token).build();

    }*/

}

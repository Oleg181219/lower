package org.lower.document.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dto.request.AuthRequest;
import org.lower.document.dto.request.ClientRequest;
import org.lower.document.dto.request.OwnerRequest;
import org.lower.document.dto.request.StaffRequest;
import org.lower.document.dto.response.AuthResponse;
import org.lower.document.dto.response.ClientResponce;
import org.lower.document.dto.response.OwnerResponse;
import org.lower.document.services.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.lower.document.util.UtilsAndConstants.AUTHORIZATION;
import static org.lower.document.util.UtilsAndConstants.BEARER;

;

@Slf4j
@RestController
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
        try {
            String token = authService.authenticate(request.getUsername(), request.getPassword());

            log.info(token);
            return ResponseEntity.ok().header(AUTHORIZATION, BEARER + token).build();
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    /**
     * Создание владельца. Нужно добавить защиту. Что бы левые не могли создаваться.
     *
     * @param request
     * @return
     */
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

    /**
     * Создание клиента. Создается только владельцем или сотрудником.
     *
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/register/client")
    public ResponseEntity<ClientResponce> client(@RequestBody ClientRequest request) {
        return ResponseEntity.ok().body(authService.createClient(request));
    }

}

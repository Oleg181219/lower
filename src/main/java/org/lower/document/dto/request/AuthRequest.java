package org.lower.document.dto.request;

import lombok.Data;

/**
 * Дто для авторизации.
 */
@Data
public class AuthRequest {
    private String username;
    private String password;
}

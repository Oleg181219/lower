package org.lower.document.dto;

import lombok.Data;

/**
 * Дто для авторизации.
 */
@Data
public class AuthRequest {
    private String username;
    private String password;
}

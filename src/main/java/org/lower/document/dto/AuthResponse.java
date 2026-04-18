package org.lower.document.dto;

import lombok.Data;
import org.lower.document.dto.enums.Role;

@Data
public class AuthResponse {
    private Role role;

}

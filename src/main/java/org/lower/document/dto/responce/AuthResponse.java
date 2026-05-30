package org.lower.document.dto.responce;

import lombok.Data;
import org.lower.document.dto.enums.Role;

@Data
public class AuthResponse {
    private Role role;

}

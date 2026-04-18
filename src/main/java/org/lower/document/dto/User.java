package org.lower.document.dto;

import lombok.Data;
import org.lower.document.dto.enums.Role;

@Data
public class User {
    private Long id;
    private String username;
    private String password; // будет хешироваться
    private Role role;

}


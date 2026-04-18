package org.lower.document.dto.enums;

public enum Role {
    ADMIN,// полный доступ. строго только кто-то из нас, отладка, поддержка, сопровождение. никому лишнему
    OWNER,// владелец
    WORKER// возможный наемный сотрудник у владельца
}

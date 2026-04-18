package org.lower.document.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Зашифровать пароль перед сохранением в БД.
     * @param rawPassword не зашифрованный пароль
     * @return зашифрованный пароль (хеш)
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Проверить, совпадает ли введённый пароль с хешем.
     * @param rawPassword введённый пользователем пароль
     * @param encodedPassword хеш, сохранённый в БД
     * @return true, если пароли совпадают
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
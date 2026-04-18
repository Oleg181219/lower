package org.lower.document.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lower.document.dao.UserDao;
import org.lower.document.jooq.codegen.tables.records.UsersRecord;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ищем пользователя в БД
        UsersRecord user = userDao.findByUsername(username);

        if (user == null) {
            log.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Преобразуем роль из строки (например, "ADMIN") в GrantedAuthority ("ROLE_ADMIN")
        // Важно: Spring Security ожидает префикс ROLE_ для метода hasRole()
        var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        // Возвращаем стандартный объект User из Spring Security
        // Пароль здесь не важен для JWT-аутентификации, так как проверка идет через токен,
        // но он должен быть не пустым, если этот сервис используется где-то еще.
        return new User(
                user.getUsername(),
                user.getPassword(), // Хеш пароля из БД
                Collections.singletonList(authority)
        );
    }
}

package org.lower.document.security;

import org.lower.document.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // Включаем @PreAuthorize, @Secured и т.д.
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CORS (или настраиваем ниже, если нужно)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Отключаем CSRF, так как используем JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Настраиваем авторизацию запросов
                .authorizeHttpRequests(auth -> auth
                        // Публичные точки (без токена)
                        .requestMatchers("/api/auth/authenticate").permitAll()
                        .requestMatchers("/api/auth/register/owner").permitAll()   // он сам проверяет заголовок ADMIN
                        .requestMatchers("/api/auth/register/staff").permitAll()   // аналогично
                        // Закрытая точка – только для ADMIN или OWNER
                        .requestMatchers("/api/auth/register/client").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers("/api/documents/getClients").hasAnyRole("ADMIN", "OWNER")
                        // Swagger и статика
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**").permitAll())

                        // Добавление JWT-фильтра
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    // Настройка CORS (разрешаем запросы с любых источников для разработки,
    // в продакшене лучше ограничить конкретными доменами)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // ИЗМЕНИТЬ ДЛЯ ПРОМА
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "ADMIN"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
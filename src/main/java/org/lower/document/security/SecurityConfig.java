package org.lower.document.security;

import jakarta.servlet.DispatcherType;
import org.lower.document.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
@EnableMethodSecurity(securedEnabled = true)
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
                // ✅ ВАЖНО: CORS должен быть ПЕРВЫМ!
                .cors(Customizer.withDefaults())

                // Отключаем CSRF, так как используем JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Настраиваем авторизацию запросов
                .authorizeHttpRequests(auth -> auth
                        // ✅ Явно разрешаем OPTIONS для всех путей
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Игнорируем async dispatch для всех URL (важно для StreamingResponseBody!)
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()

                        // Публичные точки (без токена)
                        .requestMatchers("/api/auth/authenticate","/api/full-name-forms","/api/generate_doc",
                                "/api/auth/register/owner").permitAll()

                        // Swagger и статика
                        .requestMatchers("/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/**").permitAll()

                        // Закрытые точки – только для ADMIN или OWNER
                        .requestMatchers("/api/auth/register/client").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers("/api/documents/getClients").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers("/api/auth/register/staff").hasAnyRole("ADMIN", "OWNER")
                        .requestMatchers("/api/documents/generate").hasAnyRole("ADMIN", "OWNER")

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated())

                // Добавление JWT-фильтра
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Настройка CORS через отдельный Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Используем allowedOriginPatterns вместо allowedOrigins
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Разрешаем все необходимые методы
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Разрешаем все заголовки
        configuration.setAllowedHeaders(List.of("*"));

        // Разрешаем отправку credentials (куки, токены)
        configuration.setAllowCredentials(true);

        // Разрешаем конкретные заголовки в ответах
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Disposition",
                "Content-Type",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // Кэшируем preflight запросы на 1 час
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

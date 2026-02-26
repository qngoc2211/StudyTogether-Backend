package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ❌ Tắt CSRF vì dùng JWT
                .csrf(csrf -> csrf.disable())

                // ✅ BẬT CORS
                .cors(cors -> {})

                // ❌ Không dùng session (JWT stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ PHÂN QUYỀN
                .authorizeHttpRequests(auth -> auth

                        // Cho phép login/register không cần token
                        .requestMatchers("/api/auth/**").permitAll()

                        // Admin API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // User API
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")

                        // Các request khác cần login
                        .anyRequest().authenticated()
                )

                // ✅ Thêm JWT filter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // ==============================
    // CẤU HÌNH CORS CHUẨN
    // ==============================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // ⚠ Cho phép frontend GitHub Pages
        configuration.setAllowedOrigins(List.of(
                "https://qngoc2211.github.io"
        ));

        // Cho phép tất cả method cần thiết
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        // Cho phép tất cả header
        configuration.setAllowedHeaders(List.of("*"));

        // JWT không dùng cookie → để false
        configuration.setAllowCredentials(false);

        // Cache preflight 1 giờ
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
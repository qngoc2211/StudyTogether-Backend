package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Tắt CSRF vì dùng JWT
                .csrf(csrf -> csrf.disable())
                
                // Cấu hình CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Không dùng session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Phân quyền
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - KHÔNG cần token
                        .requestMatchers(
                            "/api/auth/**"     // login, register, verify
                        ).permitAll()
                        
                        // Public endpoints - có thể xem không cần đăng nhập
                        .requestMatchers(
                            "/api/posts",
                            "/api/posts/**",
                            "/api/activities",
                            "/api/activities/**",
                            "/api/quizzes/daily",
                            "/api/quizzes/leaderboard"
                        ).permitAll()
                        
                        // User endpoints - cần đăng nhập (có token)
                        .requestMatchers(
                            "/api/posts/create",
                            "/api/posts/*/comments",
                            "/api/quizzes/submit",
                            "/api/quizzes/history",
                            "/api/activities/*/register",
                            "/api/user/**"
                        ).authenticated()
                        
                        // Admin endpoints - chỉ ADMIN mới được
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        
                        // Các request khác cũng cần đăng nhập
                        .anyRequest().authenticated()
                )
                
                // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, 
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Cho phép tất cả origin (để test)
    configuration.setAllowedOriginPatterns(List.of("*"));

    // Cho phép tất cả method
    configuration.setAllowedMethods(List.of("*"));

    // Cho phép tất cả header
    configuration.setAllowedHeaders(List.of("*"));

    // Không bật credentials để tránh conflict
    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
}
}
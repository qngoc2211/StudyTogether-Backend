package com.example.demo.security;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UsersRepository usersRepository) {
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }

    // Bỏ qua filter cho endpoint auth và OPTIONS requests
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Bỏ qua tất cả OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return path.startsWith("/api/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Không có header hoặc sai format → bỏ qua
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Bearer token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            logger.debug("Extracted token: {}", token);

            if (!jwtUtil.validateToken(token)) {
                logger.warn("Token validation failed");
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.extractUsername(token);
            logger.debug("Token username: {}", username);

            if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                Users user = usersRepository
                        .findByUsername(username)
                        .orElse(null);

                if (user != null) {
                    logger.debug("User found in DB: {}, role: {}", username, user.getRole());

                    String role = user.getRole();

                    // Đảm bảo luôn có ROLE_
                    if (!role.startsWith("ROLE_")) {
                        role = "ROLE_" + role;
                    }

                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority(role);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(authority)
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                    logger.info("Authentication set for user: {}", username);
                } else {
                    logger.warn("User not found in database: {}", username);
                }
            }

        } catch (Exception e) {
            logger.error("JWT authentication error: {}", e.getMessage(), e);
            // Không set authentication, request sẽ bị chặn
        }

        filterChain.doFilter(request, response);
    }
}
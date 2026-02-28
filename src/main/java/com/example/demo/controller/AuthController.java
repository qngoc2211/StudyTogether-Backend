package com.example.demo.controller;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {
        try {
            if (usersRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Username đã tồn tại"));
            }
            
            if (usersRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Email đã tồn tại"));
            }
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setRole("ROLE_USER");  // Format đúng cho Spring Security
            user.setPoints(0);
            
            Users savedUser = usersRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký thành công");
            response.put("userId", savedUser.getId());
            response.put("username", savedUser.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Đăng ký thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            Users user = usersRepository.findByUsername(username)
                .orElse(null);
                
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Sai tên đăng nhập hoặc mật khẩu"));
            }
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Sai tên đăng nhập hoặc mật khẩu"));
            }
            
            String token = jwtUtil.generateToken(user.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
            response.put("userId", user.getId());
            response.put("points", user.getPoints());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Đăng nhập thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.extractUsername(token);
                
                if (username != null && jwtUtil.validateToken(token)) {
                    Users user = usersRepository.findByUsername(username).orElse(null);
                    
                    if (user != null) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("username", user.getUsername());
                        response.put("role", user.getRole());
                        response.put("userId", user.getId());
                        return ResponseEntity.ok(response);
                    }
                }
            }
            
            return ResponseEntity.status(401)
                .body(Map.of("success", false, "message", "Token không hợp lệ"));
            
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(Map.of("success", false, "message", "Xác thực thất bại"));
        }
    }
}
package com.example.demo.controller;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://qngoc2211.github.io")
public class AuthController {

    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsersRepository usersRepository,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users request) {

        if (usersRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Username already exists!"));
        }

        if (usersRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already exists!"));
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setRole("ROLE_USER");
        request.setPoints(0);

        usersRepository.save(request);

        return ResponseEntity.ok(
                Map.of("message", "Register success!")
        );
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users request) {

        Optional<Users> userOptional =
                usersRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "User not found!"));
        }

        Users user = userOptional.get();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.status(401)
                    .body(Map.of("message", "Wrong password!"));
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "username", user.getUsername(),
                        "role", user.getRole()
                )
        );
    }
}
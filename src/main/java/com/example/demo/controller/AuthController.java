package com.example.demo.controller;

import com.example.demo.entity.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {

        if (usersRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest()
                    .body("Username already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);

        return ResponseEntity.ok("Register success!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users request) {

        Optional<Users> userOptional =
                usersRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("User not found!");
        }

        Users user = userOptional.get();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.status(401).body("Wrong password!");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(token);
    }
}

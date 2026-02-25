package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    @GetMapping("/hello")
    public String hello() {
        return "Backend is running!";
    }

}

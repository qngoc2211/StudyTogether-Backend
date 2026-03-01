package com.example.demo.controller.admin;

import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.repository.QuizRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final UsersRepository usersRepository;
    private final PostRepository postRepository;
    private final QuizRepository quizRepository;

    public AdminDashboardController(
            UsersRepository usersRepository,
            PostRepository postRepository,
            QuizRepository quizRepository) {

        this.usersRepository = usersRepository;
        this.postRepository = postRepository;
        this.quizRepository = quizRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, Long> dashboard() {

        Map<String, Long> stats = new HashMap<>();

        stats.put("users", usersRepository.count());
        stats.put("posts", postRepository.count());
        stats.put("quizzes", quizRepository.count());

        return stats;
    }
}
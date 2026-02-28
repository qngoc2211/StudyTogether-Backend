package com.example.demo.controller.publics;

import com.example.demo.entity.Quiz;
import com.example.demo.entity.Question;
import com.example.demo.entity.QuizResult;
import com.example.demo.entity.Users;
import com.example.demo.repository.QuizRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizResultRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.dto.QuizDTO;
import com.example.demo.dto.request.SubmitQuizRequest;
import com.example.demo.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuizResultRepository quizResultRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private QuizService quizService;

    @GetMapping("/daily")
    public ResponseEntity<QuizDTO> getDailyQuiz() {
        Quiz quiz = quizRepository.findByDate(LocalDate.now()).orElse(null);
        
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToDTO(quiz, false));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        return quizRepository.findById(id)
                .map(quiz -> ResponseEntity.ok(convertToDTO(quiz, true)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody SubmitQuizRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Users user = usersRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            request.setUserId(user.getId());
            
            QuizResult result = quizService.submitQuiz(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("score", result.getScore());
            response.put("totalQuestions", result.getTotalQuestions());
            response.put("percentage", result.getPercentage());
            response.put("message", "Nộp bài thành công!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getMyQuizHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return usersRepository.findByUsername(username)
                .map(user -> {
                    List<QuizResult> history = quizResultRepository.findByUserOrderByCompletedAtDesc(user);
                    return ResponseEntity.ok(history);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/results/{resultId}")
    public ResponseEntity<?> getQuizResult(@PathVariable Long resultId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return quizResultRepository.findById(resultId)
                .map(result -> {
                    if (!result.getUser().getUsername().equals(username)) {
                        return ResponseEntity.status(403).body("Không có quyền xem kết quả này");
                    }
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserQuizStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return usersRepository.findByUsername(username)
                .map(user -> {
                    List<QuizResult> results = quizResultRepository.findByUser(user);
                    
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("totalQuizzes", results.size());
                    stats.put("averageScore", results.stream()
                            .mapToDouble(QuizResult::getPercentage)
                            .average()
                            .orElse(0));
                    stats.put("highestScore", results.stream()
                            .mapToDouble(QuizResult::getPercentage)
                            .max()
                            .orElse(0));
                    stats.put("totalPoints", user.getPoints());  // Sửa: getPoints() thay vì getTotalPoints()
                    
                    return ResponseEntity.ok(stats);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/check/{quizId}")
    public ResponseEntity<Map<String, Boolean>> checkQuizCompleted(@PathVariable Long quizId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return usersRepository.findByUsername(username)
                .map(user -> {
                    Quiz quiz = quizRepository.findById(quizId).orElse(null);
                    boolean completed = quiz != null && 
                        quizResultRepository.findByUserAndQuiz(user, quiz).isPresent();
                    
                    Map<String, Boolean> response = new HashMap<>();
                    response.put("completed", completed);
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.ok(Map.of("completed", false)));
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        List<Users> topUsers = usersRepository.findTop10ByOrderByPointsDesc();  // Sửa method name
        
        return topUsers.stream()
                .map(user -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("username", user.getUsername());
                    entry.put("fullName", user.getFullName());
                    entry.put("totalPoints", user.getPoints());  // Sửa: getPoints()
                    entry.put("quizCount", quizResultRepository.countByUser(user));
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private QuizDTO convertToDTO(Quiz quiz, boolean includeAnswers) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setDate(quiz.getDate());
        dto.setActive(quiz.isActive());
        
        List<QuizDTO.QuestionDTO> questionDTOs = quiz.getQuestions().stream()
                .map(q -> {
                    QuizDTO.QuestionDTO qdto = new QuizDTO.QuestionDTO();
                    qdto.setId(q.getId());
                    qdto.setContent(q.getContent());
                    qdto.setOptions(q.getOptions());
                    
                    if (includeAnswers) {
                        qdto.setCorrectAnswer(q.getCorrectAnswer());
                    }
                    
                    return qdto;
                })
                .collect(Collectors.toList());
        
        dto.setQuestions(questionDTOs);
        
        return dto;
    }
}
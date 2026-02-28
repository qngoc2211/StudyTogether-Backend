package com.example.demo.controller.admin;

import com.example.demo.entity.Quiz;
import com.example.demo.entity.Question;
import com.example.demo.repository.QuizRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.QuizResultRepository;
import com.example.demo.dto.QuizDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/quizzes")
@CrossOrigin(origins = "*")
public class AdminQuizController {

    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuizResultRepository quizResultRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<QuizDTO> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        return quizRepository.findById(id)
                .map(quiz -> ResponseEntity.ok(convertToDTO(quiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        return quizRepository.findById(id)
                .map(quiz -> {
                    quiz.setTitle(quizDetails.getTitle());
                    quiz.setDescription(quizDetails.getDescription());
                    quiz.setDate(quizDetails.getDate());
                    quiz.setActive(quizDetails.isActive());
                    return ResponseEntity.ok(quizRepository.save(quiz));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        return quizRepository.findById(id)
                .map(quiz -> {
                    quizRepository.delete(quiz);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> addQuestion(@PathVariable Long quizId, @RequestBody Question question) {
        return quizRepository.findById(quizId)
                .map(quiz -> {
                    question.setQuiz(quiz);
                    return ResponseEntity.ok(questionRepository.save(question));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long questionId, @RequestBody Question questionDetails) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    question.setContent(questionDetails.getContent());
                    question.setOptions(questionDetails.getOptions());
                    question.setCorrectAnswer(questionDetails.getCorrectAnswer());
                    return ResponseEntity.ok(questionRepository.save(question));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        return questionRepository.findById(questionId)
                .map(question -> {
                    questionRepository.delete(question);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private QuizDTO convertToDTO(Quiz quiz) {
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
                    qdto.setCorrectAnswer(q.getCorrectAnswer());
                    return qdto;
                })
                .collect(Collectors.toList());
        dto.setQuestions(questionDTOs);
        
        dto.setCompletedCount(quizResultRepository.countByQuiz(quiz).intValue());
        
        return dto;
    }
}
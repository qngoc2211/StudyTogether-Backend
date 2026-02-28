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
public class AdminQuizController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    // ================= GET ALL QUIZZES =================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {

        List<QuizDTO> quizzes = quizRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(quizzes);
    }

    // ================= GET QUIZ BY ID =================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {

        return quizRepository.findById(id)
                .map(quiz -> ResponseEntity.ok(convertToDTO(quiz)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= CREATE QUIZ =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody Quiz quiz) {

        Quiz savedQuiz = quizRepository.save(quiz);

        return ResponseEntity.ok(convertToDTO(savedQuiz));
    }

    // ================= UPDATE QUIZ =================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable Long id,
            @RequestBody Quiz quizDetails) {

        return quizRepository.findById(id)
                .map(quiz -> {

                    quiz.setTitle(quizDetails.getTitle());
                    quiz.setDescription(quizDetails.getDescription());
                    quiz.setDate(quizDetails.getDate());
                    quiz.setActive(quizDetails.isActive());

                    Quiz updatedQuiz = quizRepository.save(quiz);

                    return ResponseEntity.ok(convertToDTO(updatedQuiz));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= DELETE QUIZ =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {

        return quizRepository.findById(id)
                .map(quiz -> {
                    quizRepository.delete(quiz);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= ADD QUESTION =================
    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> addQuestion(
            @PathVariable Long quizId,
            @RequestBody Question question) {

        return quizRepository.findById(quizId)
                .map(quiz -> {

                    question.setQuiz(quiz);
                    Question savedQuestion = questionRepository.save(question);

                    return ResponseEntity.ok(savedQuestion);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= UPDATE QUESTION =================
    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody Question questionDetails) {

        return questionRepository.findById(questionId)
                .map(question -> {

                    question.setContent(questionDetails.getContent());
                    question.setOptions(questionDetails.getOptions());
                    question.setCorrectAnswer(questionDetails.getCorrectAnswer());

                    Question updatedQuestion = questionRepository.save(question);

                    return ResponseEntity.ok(updatedQuestion);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= DELETE QUESTION =================
    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {

        return questionRepository.findById(questionId)
                .map(question -> {
                    questionRepository.delete(question);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= CONVERT ENTITY → DTO =================
    private QuizDTO convertToDTO(Quiz quiz) {

        QuizDTO dto = new QuizDTO();

        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setDate(quiz.getDate());
        dto.setActive(quiz.isActive());

        List<QuizDTO.QuestionDTO> questionDTOs =
                quiz.getQuestions()
                        .stream()
                        .map(q -> {
                            QuizDTO.QuestionDTO qdto =
                                    new QuizDTO.QuestionDTO();

                            qdto.setId(q.getId());
                            qdto.setContent(q.getContent());
                            qdto.setOptions(q.getOptions());
                            qdto.setCorrectAnswer(q.getCorrectAnswer());

                            return qdto;
                        })
                        .collect(Collectors.toList());

        dto.setQuestions(questionDTOs);

        Long count = quizResultRepository.countByQuiz(quiz);
        dto.setCompletedCount(count != null ? count.intValue() : 0);

        return dto;
    }
}
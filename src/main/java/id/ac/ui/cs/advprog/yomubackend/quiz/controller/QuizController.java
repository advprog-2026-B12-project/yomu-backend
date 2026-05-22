package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final ReadingService readingService;
    private final QuizService quizService;
    private final ReadingRepository readingRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizController(ReadingService readingService,
                          QuizService quizService, ReadingRepository readingRepository, QuizAttemptRepository quizAttemptRepository) {
        this.readingService = readingService;
        this.quizService = quizService;
        this.readingRepository = readingRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @GetMapping("/{readingId}")
    public Reading getQuiz(@PathVariable UUID readingId) {
        return readingService.findById(readingId);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody QuizSubmitRequest request) {
        try {
            return ResponseEntity.ok(quizService.submit(request));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public List<Reading> getAll() {
        return readingRepository.findAll();
    }

    @GetMapping("/status/{userId}/{readingId}")
    public Map<String, Object> getQuizStatus(
            @PathVariable UUID userId,
            @PathVariable UUID readingId
    ) {
        Optional<QuizAttempt> attempt = quizAttemptRepository
                .findByUserIdAndReadingId(userId, readingId);

        Map<String, Object> result = new HashMap<>();
        result.put("completed", attempt.isPresent());
        attempt.ifPresent(a -> {
            result.put("score", a.getScore());
            result.put("total", a.getTotal());
        });
        return result;
    }
}

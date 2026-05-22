package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingListItemResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.mapper.QuizResponseMapper;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizSessionService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingProgressService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@PreAuthorize("hasRole('PELAJAR') or hasRole('ADMIN')")
public class QuizController {

    private final ReadingService readingService;
    private final QuizService quizService;
    private final QuizResponseMapper responseMapper;
    private final QuizSessionService quizSessionService;
    private final ReadingProgressService readingProgressService;

    public QuizController(ReadingService readingService,
                          QuizService quizService,
                          QuizResponseMapper responseMapper,
                          QuizSessionService quizSessionService,
                          ReadingProgressService readingProgressService) {
        this.readingService = readingService;
        this.quizService = quizService;
        this.responseMapper = responseMapper;
        this.quizSessionService = quizSessionService;
        this.readingProgressService = readingProgressService;
    }

    @GetMapping("/{readingId}")
    public QuizResponse getQuiz(@PathVariable UUID readingId,
                                @AuthenticationPrincipal User user) {
        UUID userId = requireAuthenticatedUser(user);
        quizService.ensureNotCompleted(userId, readingId);
        readingProgressService.ensureOpened(userId, readingId);
        Reading reading = readingService.findById(readingId);
        quizSessionService.start(userId, readingId);
        return responseMapper.toQuizResponse(reading);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultResponse> submit(@RequestBody QuizSubmitRequest request,
                                                     @AuthenticationPrincipal User user) {
        UUID userId = requireAuthenticatedUser(user);
        return ResponseEntity.ok(quizService.submit(userId, request));
    }

    @GetMapping("/all")
    public List<ReadingListItemResponse> getAll(@AuthenticationPrincipal User user) {
        UUID userId = requireAuthenticatedUser(user);
        return readingService.findAll().stream()
                .map(reading -> responseMapper.toReadingListItem(
                        reading,
                        quizService.hasCompleted(userId, reading.getId())
                ))
                .toList();
    }

    @GetMapping("/status/{readingId}")
    public Map<String, Boolean> getQuizStatus(
            @PathVariable UUID readingId,
            @AuthenticationPrincipal User user
    ) {
        UUID userId = requireAuthenticatedUser(user);
        return Map.of("completed", quizService.hasCompleted(userId, readingId));
    }

    private UUID requireAuthenticatedUser(User user) {
        return ControllerUtils.requireUserId(user);
    }
}

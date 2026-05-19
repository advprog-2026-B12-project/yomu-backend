package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingListItemResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.mapper.QuizResponseMapper;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingProgressService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizSessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readings")
@PreAuthorize("hasRole('PELAJAR')")
public class ReadingController {

    private final ReadingService readingService;
    private final QuizService quizService;
    private final QuizResponseMapper responseMapper;
    private final QuizSessionService quizSessionService;
    private final ReadingProgressService readingProgressService;

    public ReadingController(ReadingService readingService,
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

    @GetMapping("/{id}")
    public ReadingResponse getById(@PathVariable UUID id,
                                   @AuthenticationPrincipal User user) {
        UUID userId = requireAuthenticatedUser(user);
        quizService.ensureNotCompleted(userId, id);
        quizSessionService.ensureNotStarted(userId, id);
        ReadingResponse response = responseMapper.toReadingResponse(readingService.findById(id));
        readingProgressService.markOpened(userId, id);
        return response;
    }

    @GetMapping
    public List<ReadingListItemResponse> getAll(@AuthenticationPrincipal User user) {
        UUID userId = requireAuthenticatedUser(user);
        return readingService.findAll().stream()
                .map(reading -> responseMapper.toReadingListItem(
                        reading,
                        quizService.hasCompleted(userId, reading.getId())
                ))
                .toList();
    }

    private UUID requireAuthenticatedUser(User user) {
        return ControllerUtils.requireUserId(user);
    }
}

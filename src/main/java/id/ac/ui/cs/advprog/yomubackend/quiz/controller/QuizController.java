package id.ac.ui.cs.advprog.yomubackend.quiz.controller;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuizSubmitRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuizService;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final ReadingService readingService;
    private final QuizService quizService;
    private final ReadingRepository readingRepository;

    public QuizController(ReadingService readingService,
                          QuizService quizService, ReadingRepository readingRepository) {
        this.readingService = readingService;
        this.quizService = quizService;
        this.readingRepository = readingRepository;
    }

    @GetMapping("/{readingId}")
    public Reading getQuiz(@PathVariable UUID readingId) {
        return readingService.findById(readingId);
    }

    @PostMapping("/submit")
    public QuizResultResponse submit(@RequestBody QuizSubmitRequest request) {
        return quizService.submit(request);
    }

    @GetMapping("/all")
    public List<Reading> getAll() {
        return readingRepository.findAll();
    }
}

package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuestionRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/questions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuestionController {

    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/{readingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse create(@PathVariable UUID readingId,
                                   @RequestBody QuestionRequest request) {
        Question question = new Question();
        question.setQuestionText(request.getQuestionText());

        Question saved = questionService.create(readingId, question);

        return QuestionResponse.builder()
                .id(saved.getId())
                .questionText(saved.getQuestionText())
                .build();
    }

    @GetMapping("/reading/{readingId}")
    public List<QuestionResponse> getByReading(@PathVariable UUID readingId) {
        return questionService.findByReading(readingId).stream()
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .build())
                .toList();
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID questionId) {
        questionService.delete(questionId);
    }
}
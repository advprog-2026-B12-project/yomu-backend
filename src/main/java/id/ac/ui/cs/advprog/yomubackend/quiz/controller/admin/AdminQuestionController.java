package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuestionRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.QuestionResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.mapper.QuizResponseMapper;
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
    private final QuizResponseMapper responseMapper;

    public AdminQuestionController(QuestionService questionService, QuizResponseMapper responseMapper) {
        this.questionService = questionService;
        this.responseMapper = responseMapper;
    }

    @PostMapping("/{readingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionResponse create(@PathVariable UUID readingId,
                                   @RequestBody QuestionRequest request) {
        Question question = new Question();
        question.setQuestionText(request.getQuestionText());
        return responseMapper.toQuestionResponse(questionService.create(readingId, question));
    }

    @PutMapping("/{questionId}")
    public QuestionResponse update(@PathVariable UUID questionId,
                                   @RequestBody QuestionRequest request) {
        Question question = new Question();
        question.setQuestionText(request.getQuestionText());
        return responseMapper.toQuestionResponse(questionService.update(questionId, question));
    }

    @GetMapping("/reading/{readingId}")
    public List<QuestionResponse> getByReading(@PathVariable UUID readingId) {
        return questionService.findByReading(readingId).stream()
                .map(responseMapper::toQuestionResponse)
                .toList();
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID questionId) {
        questionService.delete(questionId);
    }
}

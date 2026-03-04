package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/{readingId}")
    public Question create(@PathVariable UUID readingId,
                           @RequestBody Question question) {
        return questionService.create(readingId, question);
    }

    @GetMapping("/reading/{readingId}")
    public List<Question> getByReading(@PathVariable UUID readingId) {
        return questionService.findByReading(readingId);
    }

    @DeleteMapping("/{questionId}")
    public void delete(@PathVariable UUID questionId) {
        questionService.delete(questionId);
    }
}

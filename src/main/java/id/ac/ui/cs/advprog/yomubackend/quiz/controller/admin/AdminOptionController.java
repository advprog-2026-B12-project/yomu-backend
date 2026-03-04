package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.OptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/options")
public class AdminOptionController {

    private final OptionService optionService;

    public AdminOptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/{questionId}")
    public Option create(@PathVariable UUID questionId,
                         @RequestBody Option option) {
        return optionService.create(questionId, option);
    }

    @GetMapping("/question/{questionId}")
    public List<Option> getByQuestion(@PathVariable UUID questionId) {
        return optionService.findByQuestion(questionId);
    }

    @DeleteMapping("/{optionId}")
    public void delete(@PathVariable UUID optionId) {
        optionService.delete(optionId);
    }
}

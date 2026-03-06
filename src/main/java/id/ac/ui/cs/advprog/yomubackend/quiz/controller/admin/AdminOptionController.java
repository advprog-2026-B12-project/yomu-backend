package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.OptionRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.OptionResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/options")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOptionController {

    private final OptionService optionService;

    public AdminOptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/{questionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public OptionResponse create(@PathVariable UUID questionId,
                                 @RequestBody OptionRequest request) {
        Option option = new Option();
        option.setOptionText(request.getOptionText());
        option.setCorrect(request.isCorrect());

        Option saved = optionService.create(questionId, option);

        return OptionResponse.builder()
                .id(saved.getId())
                .optionText(saved.getOptionText())
                .isCorrect(saved.isCorrect())
                .build();
    }

    @GetMapping("/question/{questionId}")
    public List<OptionResponse> getByQuestion(@PathVariable UUID questionId) {
        return optionService.findByQuestion(questionId).stream()
                .map(o -> OptionResponse.builder()
                        .id(o.getId())
                        .optionText(o.getOptionText())
                        .isCorrect(o.isCorrect())
                        .build())
                .toList();
    }

    @DeleteMapping("/{optionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID optionId) {
        optionService.delete(optionId);
    }
}
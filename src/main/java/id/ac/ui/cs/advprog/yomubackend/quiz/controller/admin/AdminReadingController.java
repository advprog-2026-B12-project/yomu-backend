package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/readings")
public class AdminReadingController {

    private final ReadingService readingService;

    public AdminReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @PostMapping
    public ReadingResponse create(@RequestBody ReadingRequest request) {
        Reading reading = new Reading();
        reading.setTitle(request.getTitle());
        reading.setContent(request.getContent());

        Reading saved = readingService.create(reading);

        return ReadingResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }

    @GetMapping
    public List<ReadingResponse> getAll() {
        return readingService.findAll().stream()
                .map(r -> ReadingResponse.builder()
                        .id(r.getId())
                        .title(r.getTitle())
                        .content(r.getContent())
                        .build())
                .toList();
    }
}
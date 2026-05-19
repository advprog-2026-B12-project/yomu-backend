package id.ac.ui.cs.advprog.yomubackend.quiz.controller.admin;

import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingRequest;
import id.ac.ui.cs.advprog.yomubackend.quiz.dto.ReadingResponse;
import id.ac.ui.cs.advprog.yomubackend.quiz.mapper.QuizResponseMapper;
import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import id.ac.ui.cs.advprog.yomubackend.quiz.service.ReadingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/readings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReadingController {

    private final ReadingService readingService;
    private final QuizResponseMapper responseMapper;

    public AdminReadingController(ReadingService readingService, QuizResponseMapper responseMapper) {
        this.readingService = readingService;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadingResponse create(@RequestBody ReadingRequest request) {
        Reading reading = new Reading();
        reading.setTitle(request.getTitle());
        reading.setCategory(request.getCategory());
        reading.setContent(request.getContent());
        return responseMapper.toReadingResponse(readingService.create(reading));
    }

    @PutMapping("/{id}")
    public ReadingResponse update(@PathVariable UUID id,
                                  @RequestBody ReadingRequest request) {
        Reading reading = new Reading();
        reading.setTitle(request.getTitle());
        reading.setCategory(request.getCategory());
        reading.setContent(request.getContent());
        return responseMapper.toReadingResponse(readingService.update(id, reading));
    }

    @GetMapping
    public List<ReadingResponse> getAll() {
        return readingService.findAll().stream()
                .map(responseMapper::toReadingResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        readingService.delete(id);
    }
}

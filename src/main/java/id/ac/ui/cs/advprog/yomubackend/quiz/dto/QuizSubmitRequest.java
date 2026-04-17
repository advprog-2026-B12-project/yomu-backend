package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class QuizSubmitRequest {
    private UUID userId;
    private UUID readingId;
    private Map<String, String> answers;
}

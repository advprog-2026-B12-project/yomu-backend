package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuizQuestionResponse {
    private UUID id;
    private String questionText;
    private List<QuizOptionResponse> options;
}

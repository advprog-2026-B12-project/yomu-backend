package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OptionResponse {
    private UUID id;
    private String optionText;
    private boolean isCorrect;
}

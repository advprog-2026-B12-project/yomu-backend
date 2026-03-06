package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import lombok.Data;

@Data
public class OptionRequest {
    private String optionText;
    private boolean isCorrect;
}
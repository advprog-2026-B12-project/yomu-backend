package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizResultResponse {
    private int score;
    private int total;
}
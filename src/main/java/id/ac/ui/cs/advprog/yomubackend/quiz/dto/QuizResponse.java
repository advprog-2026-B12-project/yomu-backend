package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizResponse {
    private UUID id;
    private String title;
    private String content;
    private List<QuestionResponse> questions;
}
package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ReadingListItemResponse {
    private UUID id;
    private String title;
    private String category;
    private boolean completed;
}

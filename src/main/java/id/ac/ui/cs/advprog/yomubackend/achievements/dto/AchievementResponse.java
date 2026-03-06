package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AchievementResponse {
    private UUID id;
    private String name;
    private String description;
    private String iconUrl;
    private Integer points;
    private Integer milestone;
    private String eventType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
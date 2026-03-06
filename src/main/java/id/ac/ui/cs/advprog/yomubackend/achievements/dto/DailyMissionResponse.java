package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DailyMissionResponse {
    private UUID id;
    private String name;
    private String description;
    private Integer milestone;
    private String eventType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
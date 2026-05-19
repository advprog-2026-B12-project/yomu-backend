package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDailyMissionResponse {
    private UUID id;
    private UUID userId;
    private UUID missionId;
    private String missionName;
    private LocalDate dateAssigned;
    private Integer currentProgress;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

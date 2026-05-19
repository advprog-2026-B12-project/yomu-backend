package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserAchievementResponse {
    private UUID id;
    private UUID userId;
    private UUID achievementId;
    private String achievementName;
    private Integer currentProgress;
    private Boolean isUnlocked;
    private Boolean isDisplayed;
    private LocalDateTime unlockedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

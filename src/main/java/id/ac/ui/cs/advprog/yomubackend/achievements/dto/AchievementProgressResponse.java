package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AchievementProgressResponse {
    private UUID achievementId;
    private String name;
    private String description;
    private String iconUrl;
    private Integer points;
    private Integer milestone;
    private String eventType;

    private Integer currentProgress;
    private Boolean isUnlocked;
    private Boolean isDisplayed;
    private LocalDateTime unlockedAt;
}

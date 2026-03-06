package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;

@Data
public class AchievementRequest {
    private String name;
    private String description;
    private String iconUrl;
    private Integer points;
    private Integer milestone;
    private String eventType;
}
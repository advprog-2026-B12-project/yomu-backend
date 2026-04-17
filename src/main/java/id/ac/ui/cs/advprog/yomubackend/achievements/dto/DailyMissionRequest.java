package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;

@Data
public class DailyMissionRequest {
    private String name;
    private String description;
    private Integer milestone;
    private String eventType;
    private Boolean isActive;
}
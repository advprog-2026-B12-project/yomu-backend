package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventTriggerResponse {
    private List<AchievementProgressResponse> unlockedAchievements;
    private List<String> completedDailyMissions;
}

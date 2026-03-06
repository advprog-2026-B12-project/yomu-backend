package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;

import java.util.List;
import java.util.UUID;

public interface AchievementService {
    void processEvent(UUID userId, String eventType);
    Achievement createAchievement(Achievement achievement);
    List<Achievement> getAllAchievements();
    List<UserAchievement> getUserAchievements(UUID userId);
    UserAchievement toggleDisplayAchievement(UUID userAchievementId);
}
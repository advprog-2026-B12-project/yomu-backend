package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;

import java.util.List;
import java.util.UUID;

public interface AchievementService {
    Achievement createAchievement(Achievement achievement);
    Achievement updateAchievement(UUID id, Achievement achievement);
    void deleteAchievement(UUID id);
    List<Achievement> getAllAchievements();
    List<UserAchievementResponse> getUserAchievements(UUID userId);
    List<UserAchievementResponse> getPublicAchievements(UUID userId);
    List<AchievementProgressResponse> getUserAchievementProgress(UUID userId);
    UserAchievementResponse toggleDisplayAchievement(UUID userAchievementId);
}

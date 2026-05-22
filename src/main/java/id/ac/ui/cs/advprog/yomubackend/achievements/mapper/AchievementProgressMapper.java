package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import org.springframework.stereotype.Component;

@Component
public class AchievementProgressMapper {

    public AchievementProgressResponse toProgressResponse(Achievement achievement, UserAchievement progress) {
        AchievementProgressResponse response = new AchievementProgressResponse();
        response.setAchievementId(achievement.getId());
        response.setName(achievement.getName());
        response.setDescription(achievement.getDescription());
        response.setIconUrl(achievement.getIconUrl());
        response.setPoints(achievement.getPoints());
        response.setMilestone(achievement.getMilestone());
        response.setEventType(achievement.getEventType());
        response.setCurrentProgress(progress != null ? progress.getCurrentProgress() : 0);
        response.setIsUnlocked(progress != null ? progress.getIsUnlocked() : false);
        response.setIsDisplayed(progress != null ? progress.getIsDisplayed() : false);
        response.setUnlockedAt(progress != null ? progress.getUnlockedAt() : null);
        return response;
    }
}

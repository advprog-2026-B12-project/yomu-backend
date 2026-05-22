package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import org.springframework.stereotype.Component;

@Component
public class UserAchievementMapper {

    public UserAchievementResponse toResponse(UserAchievement entity) {
        UserAchievementResponse response = new UserAchievementResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setAchievementId(entity.getAchievement().getId());
        response.setAchievementName(entity.getAchievement().getName());
        response.setCurrentProgress(entity.getCurrentProgress());
        response.setIsUnlocked(entity.getIsUnlocked());
        response.setIsDisplayed(entity.getIsDisplayed());
        response.setUnlockedAt(entity.getUnlockedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}

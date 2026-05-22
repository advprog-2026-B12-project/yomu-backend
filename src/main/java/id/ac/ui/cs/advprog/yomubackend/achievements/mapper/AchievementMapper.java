package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.util.AchievementEventUtils;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {

    public Achievement toEntity(AchievementRequest request) {
        Achievement achievement = new Achievement();
        achievement.setName(request.getName());
        achievement.setDescription(request.getDescription());
        achievement.setIconUrl(request.getIconUrl());
        achievement.setPoints(request.getPoints() != null ? request.getPoints() : 0);
        achievement.setMilestone(request.getMilestone() != null ? request.getMilestone() : 1);
        achievement.setEventType(AchievementEventUtils.validateAndNormalize(request.getEventType()));
        return achievement;
    }

    public AchievementResponse toResponse(Achievement entity) {
        AchievementResponse response = new AchievementResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setIconUrl(entity.getIconUrl());
        response.setPoints(entity.getPoints());
        response.setMilestone(entity.getMilestone());
        response.setEventType(entity.getEventType());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}

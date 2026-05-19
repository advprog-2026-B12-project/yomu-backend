package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import org.springframework.stereotype.Component;

@Component
public class UserDailyMissionMapper {

    public UserDailyMissionResponse toResponse(UserDailyMission entity) {
        UserDailyMissionResponse response = new UserDailyMissionResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setMissionId(entity.getDailyMission().getId());
        response.setMissionName(entity.getDailyMission().getName());
        response.setDateAssigned(entity.getDateAssigned());
        response.setCurrentProgress(entity.getCurrentProgress());
        response.setIsCompleted(entity.getIsCompleted());
        response.setCompletedAt(entity.getCompletedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}

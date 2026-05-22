package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.util.AchievementEventUtils;
import org.springframework.stereotype.Component;

@Component
public class DailyMissionMapper {

    public DailyMission toEntity(DailyMissionRequest request) {
        DailyMission mission = new DailyMission();
        mission.setName(request.getName());
        mission.setDescription(request.getDescription());
        mission.setMilestone(request.getMilestone() != null ? request.getMilestone() : 1);
        mission.setEventType(AchievementEventUtils.validateAndNormalize(request.getEventType()));
        mission.setIsActive(request.getIsActive());
        return mission;
    }

    public DailyMissionResponse toResponse(DailyMission entity) {
        DailyMissionResponse response = new DailyMissionResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setMilestone(entity.getMilestone());
        response.setEventType(entity.getEventType());
        response.setIsActive(entity.getIsActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}

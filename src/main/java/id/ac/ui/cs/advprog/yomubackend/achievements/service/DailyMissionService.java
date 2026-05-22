package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;

import java.util.List;
import java.util.UUID;

public interface DailyMissionService {
    List<String> processDailyEvent(UUID userId, String eventType);
    DailyMission createDailyMission(DailyMission mission);
    List<DailyMission> getAllDailyMissions();
    List<DailyMission> getActiveDailyMissions();
    List<UserDailyMissionResponse> getUserDailyMissions(UUID userId);
    List<UserDailyMissionResponse> getTodayMissionsWithProgress(UUID userId);
    void rotateDailyMissions();
    DailyMission updateDailyMission(UUID id, DailyMission updatedMission);
    void deleteDailyMission(UUID id);
}

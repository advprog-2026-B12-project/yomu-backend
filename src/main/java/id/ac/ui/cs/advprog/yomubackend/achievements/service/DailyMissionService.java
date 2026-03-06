package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;

import java.util.List;
import java.util.UUID;

public interface DailyMissionService {
    void processDailyEvent(UUID userId, String eventType);
    DailyMission createDailyMission(DailyMission mission);
    List<DailyMission> getActiveDailyMissions();
    List<UserDailyMission> getUserDailyMissions(UUID userId);
    void rotateDailyMissions();
}
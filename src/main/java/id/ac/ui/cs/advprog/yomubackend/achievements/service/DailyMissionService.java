package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import java.util.UUID;

public interface DailyMissionService {
    void processDailyEvent(UUID userId, String eventType);
}
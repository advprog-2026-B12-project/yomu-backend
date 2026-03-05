package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import java.util.UUID;

public interface AchievementService {
    void processEvent(UUID userId, String eventType);
}
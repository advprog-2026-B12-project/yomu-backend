package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;

import java.util.List;
import java.util.UUID;

public interface AchievementEventService {
    List<AchievementProgressResponse> processEvent(UUID userId, String eventType);
}

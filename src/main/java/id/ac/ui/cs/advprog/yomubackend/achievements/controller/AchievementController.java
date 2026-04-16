package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.EventTriggerRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final DailyMissionService dailyMissionService; // <-- WAJIB DI-INJECT DI SINI

    @PostMapping
    public ResponseEntity<AchievementResponse> createAchievement(@RequestBody AchievementRequest request) {
        Achievement achievement = mapToEntity(request);
        Achievement savedAchievement = achievementService.createAchievement(achievement);
        return new ResponseEntity<>(mapToResponse(savedAchievement), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        List<AchievementResponse> responses = achievementService.getAllAchievements().stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAchievement>> getUserAchievements(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getUserAchievements(userId));
    }

    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<List<AchievementProgressResponse>> getUserAchievementProgress(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getUserAchievementProgress(userId));
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerEvent(@RequestBody EventTriggerRequest request) {
        String eventType = validateAndNormalizeEventType(request.getEventType());
        achievementService.processEvent(request.getUserId(), eventType);
        dailyMissionService.processDailyEvent(request.getUserId(), eventType);

        return ResponseEntity.ok("Event processed successfully for Achievements and Daily Missions");
    }

    @PutMapping("/display/{userAchievementId}")
    public ResponseEntity<UserAchievement> toggleDisplayAchievement(@PathVariable UUID userAchievementId) {
        return ResponseEntity.ok(achievementService.toggleDisplayAchievement(userAchievementId));
    }

    private Achievement mapToEntity(AchievementRequest request) {
        Achievement achievement = new Achievement();
        achievement.setName(request.getName());
        achievement.setDescription(request.getDescription());
        achievement.setIconUrl(request.getIconUrl());

        achievement.setPoints(request.getPoints() != null ? request.getPoints() : 0);
        achievement.setMilestone(request.getMilestone() != null ? request.getMilestone() : 1);
        achievement.setEventType(validateAndNormalizeEventType(request.getEventType()));
        return achievement;
    }

    private String validateAndNormalizeEventType(String eventType) {
        String normalizedEvent = AchievementEvent.normalize(eventType);
        if (!AchievementEvent.isSupported(normalizedEvent)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid eventType. Supported values: " + AchievementEvent.supportedEvents()
            );
        }
        return normalizedEvent;
    }

    private AchievementResponse mapToResponse(Achievement entity) {
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
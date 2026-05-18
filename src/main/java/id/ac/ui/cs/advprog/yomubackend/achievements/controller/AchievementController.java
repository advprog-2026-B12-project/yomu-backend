package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.EventTriggerRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.EventTriggerResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementEventService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomubackend.achievements.util.AchievementEventUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final AchievementEventService achievementEventService;
    private final DailyMissionService dailyMissionService;
    private final AchievementMapper achievementMapper;

    @PostMapping
    public ResponseEntity<AchievementResponse> createAchievement(@RequestBody AchievementRequest request) {
        Achievement savedAchievement = achievementService.createAchievement(achievementMapper.toEntity(request));
        return new ResponseEntity<>(achievementMapper.toResponse(savedAchievement), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        List<AchievementResponse> responses = achievementService.getAllAchievements().stream()
                .map(achievementMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAchievementResponse>> getUserAchievements(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getUserAchievements(userId));
    }

    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<List<AchievementProgressResponse>> getUserAchievementProgress(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getUserAchievementProgress(userId));
    }

    @PostMapping("/trigger")
    @Transactional
    public ResponseEntity<EventTriggerResponse> triggerEvent(@RequestBody EventTriggerRequest request) {
        String eventType = AchievementEventUtils.validateAndNormalize(request.getEventType());
        List<AchievementProgressResponse> unlockedAchievements =
                achievementEventService.processEvent(request.getUserId(), eventType);
        List<String> completedDailyMissions =
                dailyMissionService.processDailyEvent(request.getUserId(), eventType);

        EventTriggerResponse response = new EventTriggerResponse();
        response.setUnlockedAchievements(unlockedAchievements);
        response.setCompletedDailyMissions(completedDailyMissions);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/display/{userAchievementId}")
    public ResponseEntity<UserAchievementResponse> toggleDisplayAchievement(@PathVariable UUID userAchievementId) {
        return ResponseEntity.ok(achievementService.toggleDisplayAchievement(userAchievementId));
    }
}

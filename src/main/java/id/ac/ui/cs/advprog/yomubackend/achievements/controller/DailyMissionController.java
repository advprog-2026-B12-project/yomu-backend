package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/daily-missions")
@RequiredArgsConstructor
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;

    @PostMapping
    public ResponseEntity<DailyMissionResponse> createDailyMission(@RequestBody DailyMissionRequest request) {
        DailyMission mission = mapToEntity(request);
        DailyMission savedMission = dailyMissionService.createDailyMission(mission);
        return new ResponseEntity<>(mapToResponse(savedMission), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyMissionResponse> updateDailyMission(
            @PathVariable UUID id,
            @RequestBody DailyMissionRequest request) {
        DailyMission missionDetails = mapToEntity(request);
        DailyMission updatedMission = dailyMissionService.updateDailyMission(id, missionDetails);
        return ResponseEntity.ok(mapToResponse(updatedMission));
    }

    @GetMapping("/active")
    public ResponseEntity<List<DailyMissionResponse>> getActiveDailyMissions() {
        List<DailyMissionResponse> responses = dailyMissionService.getActiveDailyMissions().stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserDailyMission>> getUserDailyMissions(@PathVariable UUID userId) {
        return ResponseEntity.ok(dailyMissionService.getUserDailyMissions(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyMission(@PathVariable UUID id) {
        dailyMissionService.deleteDailyMission(id);
        return ResponseEntity.noContent().build();
    }

    private DailyMission mapToEntity(DailyMissionRequest request) {
        DailyMission mission = new DailyMission();
        mission.setName(request.getName());
        mission.setDescription(request.getDescription());
        mission.setMilestone(request.getMilestone() != null ? request.getMilestone() : 1);
        mission.setEventType(request.getEventType());
        mission.setIsActive(request.getIsActive() != null ? request.getIsActive() : false);
        return mission;
    }

    private DailyMissionResponse mapToResponse(DailyMission entity) {
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
package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.DailyMissionMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-missions")
@RequiredArgsConstructor
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;
    private final DailyMissionMapper dailyMissionMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyMissionResponse> createDailyMission(@RequestBody DailyMissionRequest request) {
        DailyMission savedMission = dailyMissionService.createDailyMission(dailyMissionMapper.toEntity(request));
        return new ResponseEntity<>(dailyMissionMapper.toResponse(savedMission), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyMissionResponse> updateDailyMission(
            @PathVariable UUID id,
            @RequestBody DailyMissionRequest request) {
        DailyMission updatedMission = dailyMissionService.updateDailyMission(id, dailyMissionMapper.toEntity(request));
        return ResponseEntity.ok(dailyMissionMapper.toResponse(updatedMission));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('PELAJAR')")
    public ResponseEntity<List<DailyMissionResponse>> getActiveDailyMissions() {
        List<DailyMissionResponse> responses = dailyMissionService.getActiveDailyMissions().stream()
                .map(dailyMissionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('PELAJAR')")
    public ResponseEntity<List<UserDailyMissionResponse>> getUserDailyMissions(@PathVariable UUID userId) {
        return ResponseEntity.ok(dailyMissionService.getUserDailyMissions(userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDailyMission(@PathVariable UUID id) {
        dailyMissionService.deleteDailyMission(id);
        return ResponseEntity.noContent().build();
    }
}

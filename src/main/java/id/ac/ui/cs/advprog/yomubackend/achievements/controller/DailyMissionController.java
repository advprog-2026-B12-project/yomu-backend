package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-missions")
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;

    @Autowired
    public DailyMissionController(DailyMissionService dailyMissionService) {
        this.dailyMissionService = dailyMissionService;
    }

    @PostMapping
    public ResponseEntity<DailyMission> createDailyMission(@RequestBody DailyMission mission) {
        DailyMission savedMission = dailyMissionService.createDailyMission(mission);
        return new ResponseEntity<>(savedMission, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DailyMission>> getActiveDailyMissions() {
        List<DailyMission> activeMissions = dailyMissionService.getActiveDailyMissions();
        return ResponseEntity.ok(activeMissions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserDailyMission>> getUserDailyMissions(@PathVariable UUID userId) {
        List<UserDailyMission> userMissions = dailyMissionService.getUserDailyMissions(userId);
        return ResponseEntity.ok(userMissions);
    }
}
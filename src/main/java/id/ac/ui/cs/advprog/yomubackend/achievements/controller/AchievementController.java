package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final AchievementMapper achievementMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AchievementResponse> createAchievement(@RequestBody AchievementRequest request) {
        Achievement savedAchievement = achievementService.createAchievement(achievementMapper.toEntity(request));
        return new ResponseEntity<>(achievementMapper.toResponse(savedAchievement), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AchievementResponse> updateAchievement(@PathVariable UUID id,
                                                                  @RequestBody AchievementRequest request) {
        Achievement updated = achievementService.updateAchievement(id, achievementMapper.toEntity(request));
        return ResponseEntity.ok(achievementMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAchievement(@PathVariable UUID id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        List<AchievementResponse> responses = achievementService.getAllAchievements().stream()
                .map(achievementMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    public ResponseEntity<List<UserAchievementResponse>> getUserAchievements(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getUserAchievements(userId));
    }

    @GetMapping("/user/{userId}/public")
    public ResponseEntity<List<UserAchievementResponse>> getPublicAchievements(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getPublicAchievements(userId));
    }

    @GetMapping("/user/{userId}/progress")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    public ResponseEntity<List<AchievementProgressResponse>> getUserAchievementProgress(@PathVariable UUID userId) {
        return ResponseEntity.ok(achievementService.getUserAchievementProgress(userId));
    }

    @PutMapping("/display/{userAchievementId}")
    @PreAuthorize("hasRole('PELAJAR') or hasRole('ADMIN')")
    public ResponseEntity<UserAchievementResponse> toggleDisplayAchievement(@PathVariable UUID userAchievementId) {
        return ResponseEntity.ok(achievementService.toggleDisplayAchievement(userAchievementId));
    }
}

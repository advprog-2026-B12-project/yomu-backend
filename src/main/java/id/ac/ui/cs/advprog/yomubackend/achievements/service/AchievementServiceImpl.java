package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.exception.AchievementNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.achievements.exception.UserAchievementNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.AchievementProgressMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.UserAchievementMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementProgressMapper progressMapper;
    private final UserAchievementMapper userAchievementMapper;
    private final MeterRegistry meterRegistry;

    public AchievementServiceImpl(AchievementRepository achievementRepository,
                                  UserAchievementRepository userAchievementRepository,
                                  AchievementProgressMapper progressMapper,
                                  UserAchievementMapper userAchievementMapper,
                                  MeterRegistry meterRegistry) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.progressMapper = progressMapper;
        this.userAchievementMapper = userAchievementMapper;
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Timed(value = "achievement.create.time", description = "Time taken to create an achievement")
    public Achievement createAchievement(Achievement achievement) {
        meterRegistry.counter("achievement.created.total").increment();
        return achievementRepository.save(achievement);
    }

    @Override
    public Achievement updateAchievement(UUID id, Achievement updated) {
        Achievement existing = achievementRepository.findById(id)
                .orElseThrow(() -> new AchievementNotFoundException(id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setIconUrl(updated.getIconUrl());
        existing.setPoints(updated.getPoints());
        existing.setMilestone(updated.getMilestone());
        existing.setEventType(updated.getEventType());
        return achievementRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteAchievement(UUID id) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new AchievementNotFoundException(id));
        userAchievementRepository.deleteByAchievementId(id);
        achievementRepository.delete(achievement);
    }

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> getUserAchievements(UUID userId) {
        return userAchievementRepository.findByUserId(userId).stream()
                .map(userAchievementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> getPublicAchievements(UUID userId) {
        return userAchievementRepository.findByUserIdAndIsDisplayedTrue(userId).stream()
                .map(userAchievementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Timed(value = "achievement.progress.get.time", description = "Time taken to get user achievement progress")
    public List<AchievementProgressResponse> getUserAchievementProgress(UUID userId) {
        List<Achievement> achievements = achievementRepository.findAll();
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(userId);

        Map<UUID, UserAchievement> byAchievementId = new HashMap<>();
        for (UserAchievement ua : userAchievements) {
            byAchievementId.put(ua.getAchievement().getId(), ua);
        }

        return achievements.stream()
                .map(a -> progressMapper.toProgressResponse(a, byAchievementId.get(a.getId())))
                .toList();
    }

    @Override
    @Transactional
    public UserAchievementResponse toggleDisplayAchievement(UUID userAchievementId) {
        UserAchievement userAchievement = userAchievementRepository.findById(userAchievementId)
                .orElseThrow(() -> new UserAchievementNotFoundException(userAchievementId));
        userAchievement.setIsDisplayed(!Boolean.TRUE.equals(userAchievement.getIsDisplayed()));
        return userAchievementMapper.toResponse(userAchievementRepository.save(userAchievement));
    }
}

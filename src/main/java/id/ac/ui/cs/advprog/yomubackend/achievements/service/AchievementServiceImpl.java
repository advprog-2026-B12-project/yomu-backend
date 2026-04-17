package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;

    // Dependency Injection
    @Autowired
    public AchievementServiceImpl(AchievementRepository achievementRepository,
                                  UserAchievementRepository userAchievementRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    @Override
    @Transactional
    public void processEvent(UUID userId, String eventType) {
        List<Achievement> relatedAchievements = achievementRepository.findByEventType(eventType);

        for (Achievement achievement : relatedAchievements) {
            UserAchievement userProgress = userAchievementRepository
                    .findByUserIdAndAchievementId(userId, achievement.getId())
                    .orElseGet(() -> {
                        UserAchievement newProgress = new UserAchievement();
                        newProgress.setUserId(userId);
                        newProgress.setAchievement(achievement);
                        newProgress.setCurrentProgress(0);
                        newProgress.setIsUnlocked(false);
                        newProgress.setIsDisplayed(false);
                        return newProgress;
                    });

            if (userProgress.getIsUnlocked()) {
                continue;
            }

            userProgress.setCurrentProgress(userProgress.getCurrentProgress() + 1);

            if (userProgress.getCurrentProgress() >= achievement.getMilestone()) {
                userProgress.setIsUnlocked(true); // Sah! Unlocked!
                userProgress.setUnlockedAt(LocalDateTime.now());
            }

            userAchievementRepository.save(userProgress);
        }
    }

    @Override
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    @Override
    public List<UserAchievement> getUserAchievements(UUID userId) {
        return userAchievementRepository.findByUserId(userId);
    }

    @Override
    public List<AchievementProgressResponse> getUserAchievementProgress(UUID userId) {
        List<Achievement> achievements = achievementRepository.findAll();
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(userId);

        Map<UUID, UserAchievement> userAchievementByAchievementId = new HashMap<>();
        for (UserAchievement userAchievement : userAchievements) {
            userAchievementByAchievementId.put(userAchievement.getAchievement().getId(), userAchievement);
        }

        return achievements.stream().map(achievement -> {
            UserAchievement progress = userAchievementByAchievementId.get(achievement.getId());
            AchievementProgressResponse response = new AchievementProgressResponse();

            response.setAchievementId(achievement.getId());
            response.setName(achievement.getName());
            response.setDescription(achievement.getDescription());
            response.setIconUrl(achievement.getIconUrl());
            response.setPoints(achievement.getPoints());
            response.setMilestone(achievement.getMilestone());
            response.setEventType(achievement.getEventType());

            response.setCurrentProgress(progress != null ? progress.getCurrentProgress() : 0);
            response.setIsUnlocked(progress != null ? progress.getIsUnlocked() : false);
            response.setIsDisplayed(progress != null ? progress.getIsDisplayed() : false);
            response.setUnlockedAt(progress != null ? progress.getUnlockedAt() : null);
            return response;
        }).toList();
    }

    @Override
    public UserAchievement toggleDisplayAchievement(UUID userAchievementId) {
        UserAchievement userAchievement = userAchievementRepository.findById(userAchievementId)
                .orElseThrow(() -> new IllegalArgumentException("User Achievement tidak ditemukan"));

        userAchievement.setIsDisplayed(!userAchievement.getIsDisplayed());

        return userAchievementRepository.save(userAchievement);
    }
}
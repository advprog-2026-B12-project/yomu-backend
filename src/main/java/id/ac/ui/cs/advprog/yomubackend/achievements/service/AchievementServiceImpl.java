package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
}
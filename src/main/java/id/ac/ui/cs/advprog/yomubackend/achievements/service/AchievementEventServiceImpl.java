package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.AchievementProgressMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AchievementEventServiceImpl implements AchievementEventService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementProgressMapper progressMapper;

    public AchievementEventServiceImpl(AchievementRepository achievementRepository,
                                       UserAchievementRepository userAchievementRepository,
                                       AchievementProgressMapper progressMapper) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.progressMapper = progressMapper;
    }

    @Override
    @Transactional
    public List<AchievementProgressResponse> processEvent(UUID userId, String eventType) {
        List<Achievement> relatedAchievements = achievementRepository.findByEventType(eventType);
        List<AchievementProgressResponse> newlyUnlocked = new ArrayList<>();

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
                userProgress.setIsUnlocked(true);
                userProgress.setUnlockedAt(LocalDateTime.now());
                newlyUnlocked.add(progressMapper.toProgressResponse(achievement, userProgress));
            }

            userAchievementRepository.save(userProgress);
        }

        return newlyUnlocked;
    }
}

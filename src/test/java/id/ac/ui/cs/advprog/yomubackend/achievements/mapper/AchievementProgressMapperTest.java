package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AchievementProgressMapperTest {

    private AchievementProgressMapper mapper;

    private Achievement achievement;

    @BeforeEach
    void setUp() {
        mapper = new AchievementProgressMapper();

        achievement = new Achievement();
        achievement.setId(UUID.randomUUID());
        achievement.setName("Kutu Buku");
        achievement.setDescription("Selesaikan 10 bacaan");
        achievement.setIconUrl("https://example.com/icon.png");
        achievement.setPoints(100);
        achievement.setMilestone(10);
        achievement.setEventType(AchievementEvent.READING_COMPLETED);
    }

    @Test
    void toProgressResponse_withProgress_mapsAllFields() {
        UserAchievement progress = new UserAchievement();
        progress.setCurrentProgress(7);
        progress.setIsUnlocked(false);
        progress.setIsDisplayed(true);
        progress.setUnlockedAt(null);

        AchievementProgressResponse result = mapper.toProgressResponse(achievement, progress);

        assertEquals(achievement.getId(), result.getAchievementId());
        assertEquals("Kutu Buku", result.getName());
        assertEquals("Selesaikan 10 bacaan", result.getDescription());
        assertEquals("https://example.com/icon.png", result.getIconUrl());
        assertEquals(100, result.getPoints());
        assertEquals(10, result.getMilestone());
        assertEquals(AchievementEvent.READING_COMPLETED, result.getEventType());
        assertEquals(7, result.getCurrentProgress());
        assertFalse(result.getIsUnlocked());
        assertTrue(result.getIsDisplayed());
    }

    @Test
    void toProgressResponse_withUnlockedProgress_setsUnlockedAt() {
        LocalDateTime unlockedAt = LocalDateTime.now();

        UserAchievement progress = new UserAchievement();
        progress.setCurrentProgress(10);
        progress.setIsUnlocked(true);
        progress.setIsDisplayed(true);
        progress.setUnlockedAt(unlockedAt);

        AchievementProgressResponse result = mapper.toProgressResponse(achievement, progress);

        assertTrue(result.getIsUnlocked());
        assertEquals(unlockedAt, result.getUnlockedAt());
    }

    @Test
    void toProgressResponse_withNullProgress_usesDefaults() {
        AchievementProgressResponse result = mapper.toProgressResponse(achievement, null);

        assertEquals(achievement.getId(), result.getAchievementId());
        assertEquals("Kutu Buku", result.getName());
        assertEquals(0, result.getCurrentProgress());
        assertFalse(result.getIsUnlocked());
        assertFalse(result.getIsDisplayed());
        assertNull(result.getUnlockedAt());
    }
}

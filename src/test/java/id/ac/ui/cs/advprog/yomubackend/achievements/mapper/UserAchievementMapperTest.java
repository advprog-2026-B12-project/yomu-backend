package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserAchievementMapperTest {

    private UserAchievementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserAchievementMapper();
    }

    @Test
    void toResponse_mapsAllFields() {
        Achievement achievement = new Achievement();
        achievement.setId(UUID.randomUUID());
        achievement.setName("Kutu Buku");

        UserAchievement entity = new UserAchievement();
        entity.setId(UUID.randomUUID());
        entity.setUserId(UUID.randomUUID());
        entity.setAchievement(achievement);
        entity.setCurrentProgress(5);
        entity.setIsUnlocked(true);
        entity.setIsDisplayed(true);
        entity.setUnlockedAt(LocalDateTime.now());

        UserAchievementResponse result = mapper.toResponse(entity);

        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getUserId(), result.getUserId());
        assertEquals(achievement.getId(), result.getAchievementId());
        assertEquals("Kutu Buku", result.getAchievementName());
        assertEquals(5, result.getCurrentProgress());
        assertTrue(result.getIsUnlocked());
        assertTrue(result.getIsDisplayed());
        assertNotNull(result.getUnlockedAt());
    }

    @Test
    void toResponse_handlesNotUnlocked() {
        Achievement achievement = new Achievement();
        achievement.setId(UUID.randomUUID());
        achievement.setName("Novice Reader");

        UserAchievement entity = new UserAchievement();
        entity.setId(UUID.randomUUID());
        entity.setUserId(UUID.randomUUID());
        entity.setAchievement(achievement);
        entity.setCurrentProgress(2);
        entity.setIsUnlocked(false);
        entity.setIsDisplayed(false);
        entity.setUnlockedAt(null);

        UserAchievementResponse result = mapper.toResponse(entity);

        assertFalse(result.getIsUnlocked());
        assertFalse(result.getIsDisplayed());
        assertNull(result.getUnlockedAt());
    }
}

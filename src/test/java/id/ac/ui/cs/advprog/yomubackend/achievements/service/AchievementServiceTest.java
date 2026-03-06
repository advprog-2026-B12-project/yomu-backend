package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @InjectMocks
    private AchievementServiceImpl achievementService;

    private UUID dummyUserId;
    private Achievement dummyAchievement;

    @BeforeEach
    void setUp() {
        dummyUserId = UUID.randomUUID();

        dummyAchievement = new Achievement();
        dummyAchievement.setId(UUID.randomUUID());
        dummyAchievement.setName("Kutu Buku Pemula");
        dummyAchievement.setMilestone(1);
        dummyAchievement.setEventType(AchievementEvent.READING_COMPLETED);
    }

    @Test
    void testProcessEvent_ShouldIncrementProgressAndUnlock_WhenMilestoneReached() {
        when(achievementRepository.findByEventType(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(dummyAchievement));

        when(userAchievementRepository.findByUserIdAndAchievementId(dummyUserId, dummyAchievement.getId()))
                .thenReturn(Optional.empty());

        achievementService.processEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));

        verify(userAchievementRepository).save(argThat(userAchievement -> {
            assertEquals(dummyUserId, userAchievement.getUserId());
            assertEquals(1, userAchievement.getCurrentProgress());
            assertTrue(userAchievement.getIsUnlocked());
            assertNotNull(userAchievement.getUnlockedAt());
            return true;
        }));
    }

    @Test
    void testToggleDisplayAchievement_ShouldToggleIsDisplayed() {
        UserAchievement ua = new UserAchievement();
        ua.setId(UUID.randomUUID());
        ua.setIsDisplayed(false);

        when(userAchievementRepository.findById(ua.getId())).thenReturn(java.util.Optional.of(ua));
        when(userAchievementRepository.save(any(UserAchievement.class))).thenReturn(ua);

        UserAchievement result = achievementService.toggleDisplayAchievement(ua.getId());

        assertTrue(result.getIsDisplayed());
        verify(userAchievementRepository, times(1)).save(ua);
    }

    @Test
    void testCreateAchievement_ShouldReturnSavedAchievement() {
        when(achievementRepository.save(any(Achievement.class))).thenReturn(dummyAchievement);
        Achievement result = achievementService.createAchievement(new Achievement());
        assertNotNull(result);
        assertEquals(dummyAchievement.getName(), result.getName());
    }

    @Test
    void testGetAllAchievements_ShouldReturnList() {
        when(achievementRepository.findAll()).thenReturn(List.of(dummyAchievement));
        List<Achievement> result = achievementService.getAllAchievements();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetUserAchievements_ShouldReturnList() {
        UserAchievement ua = new UserAchievement();
        ua.setUserId(dummyUserId);
        when(userAchievementRepository.findByUserId(dummyUserId)).thenReturn(List.of(ua));

        List<UserAchievement> result = achievementService.getUserAchievements(dummyUserId);
        assertEquals(1, result.size());
        assertEquals(dummyUserId, result.get(0).getUserId());
    }

    @Test
    void testToggleDisplayAchievement_ShouldThrowException_WhenNotFound() {
        UUID randomId = UUID.randomUUID();
        when(userAchievementRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> achievementService.toggleDisplayAchievement(randomId));
    }

    @Test
    void testProcessEvent_ShouldDoNothing_WhenNoAchievementFound() {
        when(achievementRepository.findByEventType("UNKNOWN_EVENT")).thenReturn(List.of());

        achievementService.processEvent(dummyUserId, "UNKNOWN_EVENT");

        verify(userAchievementRepository, never()).save(any());
    }
}
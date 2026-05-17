package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
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
class AchievementServiceTest {

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
    void testProcessEvent_ShouldReturnUnlockedAchievement_WhenMilestoneReached() {
        when(achievementRepository.findByEventType(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(dummyAchievement));

        when(userAchievementRepository.findByUserIdAndAchievementId(dummyUserId, dummyAchievement.getId()))
                .thenReturn(Optional.empty());

        List<AchievementProgressResponse> result =
                achievementService.processEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));

        // Must return exactly the achievement that was just unlocked
        assertEquals(1, result.size());
        assertEquals(dummyAchievement.getId(), result.get(0).getAchievementId());
        assertEquals(dummyAchievement.getName(), result.get(0).getName());
        assertTrue(result.get(0).getIsUnlocked());
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
        ua.setAchievement(dummyAchievement);
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
    void testProcessEvent_ShouldReturnEmptyList_WhenNoAchievementFound() {
        when(achievementRepository.findByEventType("UNKNOWN_EVENT")).thenReturn(List.of());

        List<AchievementProgressResponse> result =
                achievementService.processEvent(dummyUserId, "UNKNOWN_EVENT");

        verify(userAchievementRepository, never()).save(any());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessEvent_ShouldReturnEmptyList_WhenAlreadyUnlocked() {
        UserAchievement alreadyUnlocked = new UserAchievement();
        alreadyUnlocked.setUserId(dummyUserId);
        alreadyUnlocked.setAchievement(dummyAchievement);
        alreadyUnlocked.setCurrentProgress(1);
        alreadyUnlocked.setIsUnlocked(true);

        when(achievementRepository.findByEventType(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(dummyAchievement));
        when(userAchievementRepository.findByUserIdAndAchievementId(dummyUserId, dummyAchievement.getId()))
                .thenReturn(Optional.of(alreadyUnlocked));

        List<AchievementProgressResponse> result =
                achievementService.processEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        verify(userAchievementRepository, never()).save(any());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessEvent_ShouldReturnEmptyList_WhenMilestoneNotYetReached() {
        Achievement highMilestone = new Achievement();
        highMilestone.setId(UUID.randomUUID());
        highMilestone.setName("Quiz Master");
        highMilestone.setMilestone(5);
        highMilestone.setEventType(AchievementEvent.QUIZ_FINISHED);

        when(achievementRepository.findByEventType(AchievementEvent.QUIZ_FINISHED))
                .thenReturn(List.of(highMilestone));
        when(userAchievementRepository.findByUserIdAndAchievementId(dummyUserId, highMilestone.getId()))
                .thenReturn(Optional.empty());

        List<AchievementProgressResponse> result =
                achievementService.processEvent(dummyUserId, AchievementEvent.QUIZ_FINISHED);

        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserAchievementProgress_ShouldReturnAllAchievementsWithDefaultProgress() {
        Achievement anotherAchievement = new Achievement();
        anotherAchievement.setId(UUID.randomUUID());
        anotherAchievement.setName("Quiz Finisher");
        anotherAchievement.setMilestone(3);
        anotherAchievement.setEventType(AchievementEvent.QUIZ_FINISHED);

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUserId(dummyUserId);
        userAchievement.setAchievement(dummyAchievement);
        userAchievement.setCurrentProgress(1);
        userAchievement.setIsUnlocked(true);
        userAchievement.setIsDisplayed(true);

        when(achievementRepository.findAll()).thenReturn(List.of(dummyAchievement, anotherAchievement));
        when(userAchievementRepository.findByUserId(dummyUserId)).thenReturn(List.of(userAchievement));

        List<AchievementProgressResponse> result = achievementService.getUserAchievementProgress(dummyUserId);

        assertEquals(2, result.size());
        assertEquals(dummyAchievement.getId(), result.get(0).getAchievementId());
        assertEquals(1, result.get(0).getCurrentProgress());
        assertTrue(result.get(0).getIsUnlocked());
        assertEquals(anotherAchievement.getId(), result.get(1).getAchievementId());
        assertEquals(0, result.get(1).getCurrentProgress());
        assertFalse(result.get(1).getIsUnlocked());
    }
}
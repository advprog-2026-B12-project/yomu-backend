package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.exception.UserAchievementNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.AchievementProgressMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.UserAchievementMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievement;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

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

    @Mock
    private AchievementProgressMapper progressMapper;

    @Mock
    private UserAchievementMapper userAchievementMapper;

    @Spy
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

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

        UserAchievementResponse uaResponse = new UserAchievementResponse();
        uaResponse.setUserId(dummyUserId);

        when(userAchievementRepository.findByUserId(dummyUserId)).thenReturn(List.of(ua));
        when(userAchievementMapper.toResponse(ua)).thenReturn(uaResponse);

        List<UserAchievementResponse> result = achievementService.getUserAchievements(dummyUserId);
        assertEquals(1, result.size());
        assertEquals(dummyUserId, result.get(0).getUserId());
    }

    @Test
    void testToggleDisplayAchievement_ShouldToggleIsDisplayed() {
        UserAchievement ua = new UserAchievement();
        ua.setId(UUID.randomUUID());
        ua.setIsDisplayed(false);
        ua.setAchievement(dummyAchievement);

        UserAchievementResponse uaResponse = new UserAchievementResponse();
        uaResponse.setIsDisplayed(true);

        when(userAchievementRepository.findById(ua.getId())).thenReturn(Optional.of(ua));
        when(userAchievementRepository.save(any(UserAchievement.class))).thenReturn(ua);
        when(userAchievementMapper.toResponse(ua)).thenReturn(uaResponse);

        UserAchievementResponse result = achievementService.toggleDisplayAchievement(ua.getId());

        assertTrue(result.getIsDisplayed());
        verify(userAchievementRepository, times(1)).save(ua);
    }

    @Test
    void testToggleDisplayAchievement_ShouldThrowException_WhenNotFound() {
        UUID randomId = UUID.randomUUID();
        when(userAchievementRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(UserAchievementNotFoundException.class,
                () -> achievementService.toggleDisplayAchievement(randomId));
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

        AchievementProgressResponse response1 = new AchievementProgressResponse();
        response1.setAchievementId(dummyAchievement.getId());
        response1.setCurrentProgress(1);
        response1.setIsUnlocked(true);

        AchievementProgressResponse response2 = new AchievementProgressResponse();
        response2.setAchievementId(anotherAchievement.getId());
        response2.setCurrentProgress(0);
        response2.setIsUnlocked(false);

        when(achievementRepository.findAll()).thenReturn(List.of(dummyAchievement, anotherAchievement));
        when(userAchievementRepository.findByUserId(dummyUserId)).thenReturn(List.of(userAchievement));
        when(progressMapper.toProgressResponse(dummyAchievement, userAchievement)).thenReturn(response1);
        when(progressMapper.toProgressResponse(anotherAchievement, null)).thenReturn(response2);

        List<AchievementProgressResponse> result = achievementService.getUserAchievementProgress(dummyUserId);

        assertEquals(2, result.size());
        assertEquals(dummyAchievement.getId(), result.get(0).getAchievementId());
        assertEquals(1, result.get(0).getCurrentProgress());
        assertTrue(result.get(0).getIsUnlocked());
        assertEquals(anotherAchievement.getId(), result.get(1).getAchievementId());
        assertEquals(0, result.get(1).getCurrentProgress());
        assertFalse(result.get(1).getIsUnlocked());
    }

    // ── AchievementEvent constant ─────────────────────────────────────────────

    @Test
    void achievementEvent_isSupported_nullReturnsFalse() {
        assertFalse(AchievementEvent.isSupported(null));
    }

    @Test
    void achievementEvent_isSupported_validReturnsTrue() {
        assertTrue(AchievementEvent.isSupported(AchievementEvent.READING_COMPLETED));
    }

    @Test
    void achievementEvent_isSupported_unknownReturnsFalse() {
        assertFalse(AchievementEvent.isSupported("UNKNOWN_EVENT"));
    }

    @Test
    void achievementEvent_isSupported_mixedCaseReturnsTrue() {
        assertTrue(AchievementEvent.isSupported("reading_completed"));
    }

    @Test
    void achievementEvent_normalize_nullReturnsNull() {
        assertNull(AchievementEvent.normalize(null));
    }

    @Test
    void achievementEvent_normalize_lowercaseReturnsUpperCase() {
        assertEquals("READING_COMPLETED", AchievementEvent.normalize("reading_completed"));
    }

    @Test
    void achievementEvent_supportedEvents_containsAllFive() {
        java.util.Set<String> events = AchievementEvent.supportedEvents();
        assertNotNull(events);
        assertEquals(5, events.size());
        assertTrue(events.contains(AchievementEvent.QUIZ_FINISHED));
        assertTrue(events.contains(AchievementEvent.PERFECT_QUIZ_SCORE));
        assertTrue(events.contains(AchievementEvent.CLAN_PROMOTION));
        assertTrue(events.contains(AchievementEvent.LOGIN_STREAK));
    }

    @Test
    void testUpdateAchievement_ShouldUpdateFields() {
        Achievement updated = new Achievement();
        updated.setName("Updated Name");
        updated.setDescription("Updated Desc");
        updated.setIconUrl("http://icon.url");
        updated.setPoints(20);
        updated.setMilestone(5);
        updated.setEventType(AchievementEvent.QUIZ_FINISHED);

        when(achievementRepository.findById(dummyAchievement.getId())).thenReturn(java.util.Optional.of(dummyAchievement));
        when(achievementRepository.save(any(Achievement.class))).thenReturn(dummyAchievement);

        Achievement result = achievementService.updateAchievement(dummyAchievement.getId(), updated);

        assertNotNull(result);
        verify(achievementRepository).save(dummyAchievement);
    }

    @Test
    void testUpdateAchievement_ShouldThrow_WhenNotFound() {
        UUID randomId = UUID.randomUUID();
        when(achievementRepository.findById(randomId)).thenReturn(java.util.Optional.empty());

        id.ac.ui.cs.advprog.yomubackend.achievements.exception.AchievementNotFoundException ex =
                assertThrows(
                        id.ac.ui.cs.advprog.yomubackend.achievements.exception.AchievementNotFoundException.class,
                        () -> achievementService.updateAchievement(randomId, new Achievement())
                );
        assertTrue(ex.getMessage().contains(randomId.toString()));
    }

    @Test
    void testDeleteAchievement_ShouldDelete_WhenExists() {
        when(achievementRepository.findById(dummyAchievement.getId())).thenReturn(Optional.of(dummyAchievement));

        achievementService.deleteAchievement(dummyAchievement.getId());

        verify(userAchievementRepository).deleteByAchievementId(dummyAchievement.getId());
        verify(achievementRepository).delete(dummyAchievement);
    }

    @Test
    void testDeleteAchievement_ShouldThrow_WhenNotFound() {
        UUID randomId = UUID.randomUUID();
        when(achievementRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(
                id.ac.ui.cs.advprog.yomubackend.achievements.exception.AchievementNotFoundException.class,
                () -> achievementService.deleteAchievement(randomId)
        );
    }

    @Test
    void testGetPublicAchievements_ShouldReturnOnlyDisplayed() {
        UserAchievement ua = new UserAchievement();
        ua.setUserId(dummyUserId);
        ua.setAchievement(dummyAchievement);
        ua.setIsDisplayed(true);

        id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse uaResponse =
                new id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse();
        uaResponse.setUserId(dummyUserId);
        uaResponse.setIsDisplayed(true);

        when(userAchievementRepository.findByUserIdAndIsDisplayedTrue(dummyUserId)).thenReturn(List.of(ua));
        when(userAchievementMapper.toResponse(ua)).thenReturn(uaResponse);

        List<id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse> result =
                achievementService.getPublicAchievements(dummyUserId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsDisplayed());
    }

    @Test
    void testToggleDisplayAchievement_WhenIsDisplayedIsTrue_SetsToFalse() {
        UserAchievement ua = new UserAchievement();
        ua.setId(UUID.randomUUID());
        ua.setIsDisplayed(true);
        ua.setAchievement(dummyAchievement);

        id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse uaResponse =
                new id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse();
        uaResponse.setIsDisplayed(false);

        when(userAchievementRepository.findById(ua.getId())).thenReturn(java.util.Optional.of(ua));
        when(userAchievementRepository.save(any(UserAchievement.class))).thenReturn(ua);
        when(userAchievementMapper.toResponse(ua)).thenReturn(uaResponse);

        id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse result =
                achievementService.toggleDisplayAchievement(ua.getId());

        assertFalse(result.getIsDisplayed());
    }

    @Test
    void testToggleDisplayAchievement_WhenIsDisplayedIsNull_SetsToTrue() {
        UserAchievement ua = new UserAchievement();
        ua.setId(UUID.randomUUID());
        ua.setIsDisplayed(null);
        ua.setAchievement(dummyAchievement);

        id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse uaResponse =
                new id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse();
        uaResponse.setIsDisplayed(true);

        when(userAchievementRepository.findById(ua.getId())).thenReturn(java.util.Optional.of(ua));
        when(userAchievementRepository.save(any(UserAchievement.class))).thenReturn(ua);
        when(userAchievementMapper.toResponse(ua)).thenReturn(uaResponse);

        id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse result =
                achievementService.toggleDisplayAchievement(ua.getId());

        assertTrue(result.getIsDisplayed());
    }

    @Test
    void testAchievementNotFoundException_MessageContainsId() {
        UUID id = UUID.randomUUID();
        id.ac.ui.cs.advprog.yomubackend.achievements.exception.AchievementNotFoundException ex =
                new id.ac.ui.cs.advprog.yomubackend.achievements.exception.AchievementNotFoundException(id);
        assertTrue(ex.getMessage().contains(id.toString()));
    }
}

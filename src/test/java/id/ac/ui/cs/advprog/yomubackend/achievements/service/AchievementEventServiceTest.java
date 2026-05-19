package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementProgressResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.AchievementProgressMapper;
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
class AchievementEventServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private AchievementProgressMapper progressMapper;

    @InjectMocks
    private AchievementEventServiceImpl achievementEventService;

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
        AchievementProgressResponse unlockedResponse = new AchievementProgressResponse();
        unlockedResponse.setAchievementId(dummyAchievement.getId());
        unlockedResponse.setName(dummyAchievement.getName());
        unlockedResponse.setIsUnlocked(true);

        when(achievementRepository.findByEventType(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(dummyAchievement));
        when(userAchievementRepository.findByUserIdAndAchievementId(dummyUserId, dummyAchievement.getId()))
                .thenReturn(Optional.empty());
        when(progressMapper.toProgressResponse(any(Achievement.class), any(UserAchievement.class)))
                .thenReturn(unlockedResponse);

        List<AchievementProgressResponse> result =
                achievementEventService.processEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));
        assertEquals(1, result.size());
        assertEquals(dummyAchievement.getId(), result.get(0).getAchievementId());
        assertTrue(result.get(0).getIsUnlocked());
    }

    @Test
    void testProcessEvent_ShouldReturnEmptyList_WhenNoAchievementFound() {
        when(achievementRepository.findByEventType("UNKNOWN_EVENT")).thenReturn(List.of());

        List<AchievementProgressResponse> result =
                achievementEventService.processEvent(dummyUserId, "UNKNOWN_EVENT");

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
                achievementEventService.processEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

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
                achievementEventService.processEvent(dummyUserId, AchievementEvent.QUIZ_FINISHED);

        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

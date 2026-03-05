package id.ac.ui.cs.advprog.yomubackend.achievements.service;

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
        dummyAchievement.setEventType("READING_COMPLETED");
    }

    @Test
    void testProcessEvent_ShouldIncrementProgressAndUnlock_WhenMilestoneReached() {
        when(achievementRepository.findByEventType("READING_COMPLETED"))
                .thenReturn(List.of(dummyAchievement));

        when(userAchievementRepository.findByUserIdAndAchievementId(dummyUserId, dummyAchievement.getId()))
                .thenReturn(Optional.empty());

        achievementService.processEvent(dummyUserId, "READING_COMPLETED");

        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));

        verify(userAchievementRepository).save(argThat(userAchievement -> {
            assertEquals(dummyUserId, userAchievement.getUserId());
            assertEquals(1, userAchievement.getCurrentProgress());
            assertTrue(userAchievement.getIsUnlocked());
            assertNotNull(userAchievement.getUnlockedAt());
            return true;
        }));
    }
}
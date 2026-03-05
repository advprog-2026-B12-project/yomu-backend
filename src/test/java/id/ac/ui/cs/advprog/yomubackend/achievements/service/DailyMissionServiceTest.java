package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyMissionServiceTest {

    @Mock
    private DailyMissionRepository dailyMissionRepository;

    @Mock
    private UserDailyMissionRepository userDailyMissionRepository;

    @InjectMocks
    private DailyMissionServiceImpl dailyMissionService;

    private UUID dummyUserId;
    private DailyMission dummyMission;

    @BeforeEach
    void setUp() {
        dummyUserId = UUID.randomUUID();

        dummyMission = new DailyMission();
        dummyMission.setId(UUID.randomUUID());
        dummyMission.setName("Membaca Berita");
        dummyMission.setMilestone(3);
        dummyMission.setEventType("READING_COMPLETED");
        dummyMission.setIsActive(true);
    }

    @Test
    void testProcessDailyEvent_ShouldCreateNewProgress_WhenFirstTimeToday() {
        when(dailyMissionRepository.findByEventTypeAndIsActiveTrue("READING_COMPLETED"))
                .thenReturn(List.of(dummyMission));

        when(userDailyMissionRepository.findByUserIdAndDailyMissionIdAndDateAssigned(
                eq(dummyUserId), eq(dummyMission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        dailyMissionService.processDailyEvent(dummyUserId, "READING_COMPLETED");

        verify(userDailyMissionRepository, times(1)).save(argThat(progress -> {
            assertEquals(dummyUserId, progress.getUserId());
            assertEquals(1, progress.getCurrentProgress());
            assertFalse(progress.getIsCompleted());
            assertEquals(LocalDate.now(), progress.getDateAssigned());
            return true;
        }));
    }
}
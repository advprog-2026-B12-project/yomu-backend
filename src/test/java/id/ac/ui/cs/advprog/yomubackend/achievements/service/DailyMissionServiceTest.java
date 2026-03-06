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

    @Test
    void testRotateDailyMissions_ShouldActivateExactlyOneMission() {
        DailyMission m1 = new DailyMission(); m1.setId(UUID.randomUUID()); m1.setIsActive(true);
        DailyMission m2 = new DailyMission(); m2.setId(UUID.randomUUID()); m2.setIsActive(true);
        DailyMission m3 = new DailyMission(); m3.setId(UUID.randomUUID()); m3.setIsActive(false);
        DailyMission m4 = new DailyMission(); m4.setId(UUID.randomUUID()); m4.setIsActive(false);
        DailyMission m5 = new DailyMission(); m5.setId(UUID.randomUUID()); m5.setIsActive(false);

        List<DailyMission> allMissions = new java.util.ArrayList<>(List.of(m1, m2, m3, m4, m5));
        when(dailyMissionRepository.findAll()).thenReturn(allMissions);

        dailyMissionService.rotateDailyMissions();

        verify(dailyMissionRepository, times(1)).saveAll(argThat(missions -> {
            long activeCount = ((List<DailyMission>) missions).stream()
                    .filter(DailyMission::getIsActive)
                    .count();
            return activeCount == 1;
        }));
    }
}
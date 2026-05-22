package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.exception.DailyMissionNotFoundException;
import id.ac.ui.cs.advprog.yomubackend.achievements.mapper.UserDailyMissionMapper;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
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
class DailyMissionServiceTest {

    @Mock
    private DailyMissionRepository dailyMissionRepository;

    @Mock
    private UserDailyMissionRepository userDailyMissionRepository;

    @Mock
    private UserDailyMissionMapper userDailyMissionMapper;

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
        dummyMission.setEventType(AchievementEvent.READING_COMPLETED);
        dummyMission.setIsActive(true);
    }

    @Test
    void testProcessDailyEvent_ShouldCreateNewProgress_WhenFirstTimeToday() {
        when(dailyMissionRepository.findByEventTypeAndIsActiveTrue(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(dummyMission));

        when(userDailyMissionRepository.findByUserIdAndDailyMissionIdAndDateAssigned(
                eq(dummyUserId), eq(dummyMission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        List<String> result = dailyMissionService.processDailyEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        verify(userDailyMissionRepository, times(1)).save(argThat(progress -> {
            assertEquals(dummyUserId, progress.getUserId());
            assertEquals(1, progress.getCurrentProgress());
            assertFalse(progress.getIsCompleted());
            assertEquals(LocalDate.now(), progress.getDateAssigned());
            return true;
        }));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessDailyEvent_ShouldReturnMissionName_WhenMilestoneReached() {
        DailyMission easyMission = new DailyMission();
        easyMission.setId(UUID.randomUUID());
        easyMission.setName("Quick Read");
        easyMission.setMilestone(1);
        easyMission.setEventType(AchievementEvent.READING_COMPLETED);
        easyMission.setIsActive(true);

        when(dailyMissionRepository.findByEventTypeAndIsActiveTrue(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(easyMission));

        when(userDailyMissionRepository.findByUserIdAndDailyMissionIdAndDateAssigned(
                eq(dummyUserId), eq(easyMission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        List<String> result = dailyMissionService.processDailyEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        assertEquals(1, result.size());
        assertEquals("Quick Read", result.get(0));
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

    @Test
    void testUpdateDailyMission_ShouldUpdateExistingMission() {
        DailyMission existing = new DailyMission();
        existing.setId(UUID.randomUUID());
        existing.setName("Lama");

        DailyMission updatedData = new DailyMission();
        updatedData.setName("Baru");
        updatedData.setMilestone(5);

        when(dailyMissionRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(dailyMissionRepository.save(any(DailyMission.class))).thenReturn(existing);

        DailyMission result = dailyMissionService.updateDailyMission(existing.getId(), updatedData);

        assertEquals("Baru", result.getName());
        assertEquals(5, result.getMilestone());
    }

    @Test
    void testDeleteDailyMission_ShouldCallDelete() {
        UUID id = UUID.randomUUID();
        when(dailyMissionRepository.existsById(id)).thenReturn(true);
        doNothing().when(dailyMissionRepository).deleteById(id);

        dailyMissionService.deleteDailyMission(id);

        verify(dailyMissionRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteDailyMission_NotFound_ShouldThrow() {
        UUID id = UUID.randomUUID();
        when(dailyMissionRepository.existsById(id)).thenReturn(false);

        assertThrows(DailyMissionNotFoundException.class,
                () -> dailyMissionService.deleteDailyMission(id));
    }

    @Test
    void testCreateDailyMission_ShouldReturnSavedMission() {
        when(dailyMissionRepository.save(any(DailyMission.class))).thenReturn(dummyMission);
        DailyMission result = dailyMissionService.createDailyMission(new DailyMission());
        assertNotNull(result);
    }

    @Test
    void testGetActiveDailyMissions_ShouldReturnList() {
        when(dailyMissionRepository.findByIsActiveTrue()).thenReturn(List.of(dummyMission));
        List<DailyMission> result = dailyMissionService.getActiveDailyMissions();
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetUserDailyMissions_ShouldReturnList() {
        UserDailyMission udm = new UserDailyMission();
        UserDailyMissionResponse response = new UserDailyMissionResponse();

        when(userDailyMissionRepository.findByUserIdAndDateAssigned(eq(dummyUserId), any(LocalDate.class))).thenReturn(List.of(udm));
        when(userDailyMissionMapper.toResponse(udm)).thenReturn(response);

        List<UserDailyMissionResponse> result = dailyMissionService.getUserDailyMissions(dummyUserId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateDailyMission_ShouldThrowException_WhenNotFound() {
        UUID randomId = UUID.randomUUID();
        when(dailyMissionRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(DailyMissionNotFoundException.class, () ->
                dailyMissionService.updateDailyMission(randomId, new DailyMission())
        );
    }

    @Test
    void testProcessDailyEvent_ShouldReturnEmptyList_WhenNoActiveMissionFound() {
        when(dailyMissionRepository.findByEventTypeAndIsActiveTrue("UNKNOWN")).thenReturn(List.of());

        List<String> result = dailyMissionService.processDailyEvent(dummyUserId, "UNKNOWN");

        verify(userDailyMissionRepository, never()).save(any());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testRotateDailyMissions_EmptyList_ReturnsWithoutSaving() {
        when(dailyMissionRepository.findAll()).thenReturn(List.of());

        dailyMissionService.rotateDailyMissions();

        verify(dailyMissionRepository, never()).saveAll(any());
    }

    @Test
    void testProcessDailyEvent_AlreadyCompleted_SkipsProgress() {
        id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission alreadyCompleted =
                new id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission();
        alreadyCompleted.setUserId(dummyUserId);
        alreadyCompleted.setDailyMission(dummyMission);
        alreadyCompleted.setDateAssigned(LocalDate.now());
        alreadyCompleted.setCurrentProgress(3);
        alreadyCompleted.setIsCompleted(true);

        when(dailyMissionRepository.findByEventTypeAndIsActiveTrue(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(dummyMission));
        when(userDailyMissionRepository.findByUserIdAndDailyMissionIdAndDateAssigned(
                eq(dummyUserId), eq(dummyMission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(alreadyCompleted));

        List<String> result = dailyMissionService.processDailyEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        verify(userDailyMissionRepository, never()).save(any());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testProcessDailyEvent_ShouldSkip_WhenMissionAlreadyCompleted() {
        when(dailyMissionRepository.findByEventTypeAndIsActiveTrue(AchievementEvent.READING_COMPLETED))
                .thenReturn(List.of(dummyMission));

        UserDailyMission completedProgress = new UserDailyMission();
        completedProgress.setUserId(dummyUserId);
        completedProgress.setDailyMission(dummyMission);
        completedProgress.setCurrentProgress(3);
        completedProgress.setIsCompleted(true);

        when(userDailyMissionRepository.findByUserIdAndDailyMissionIdAndDateAssigned(
                eq(dummyUserId), eq(dummyMission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(completedProgress));

        List<String> result = dailyMissionService.processDailyEvent(dummyUserId, AchievementEvent.READING_COMPLETED);

        verify(userDailyMissionRepository, never()).save(any());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testRotateDailyMissions_ShouldDoNothing_WhenNoMissions() {
        when(dailyMissionRepository.findAll()).thenReturn(List.of());

        dailyMissionService.rotateDailyMissions();

        verify(dailyMissionRepository, never()).saveAll(any());
    }

    @Test
    void testGetTodayMissionsWithProgress_WithExistingProgress() {
        UserDailyMission existing = new UserDailyMission();
        existing.setUserId(dummyUserId);
        existing.setDailyMission(dummyMission);
        existing.setDateAssigned(LocalDate.now());
        existing.setCurrentProgress(2);
        existing.setIsCompleted(false);

        UserDailyMissionResponse response = new UserDailyMissionResponse();
        response.setCurrentProgress(2);

        when(dailyMissionRepository.findByIsActiveTrue()).thenReturn(List.of(dummyMission));
        when(userDailyMissionRepository.findByUserIdAndDailyMissionIdAndDateAssigned(
                eq(dummyUserId), eq(dummyMission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(existing));
        when(userDailyMissionMapper.toResponse(existing)).thenReturn(response);

        List<UserDailyMissionResponse> result = dailyMissionService.getTodayMissionsWithProgress(dummyUserId);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getCurrentProgress());
    }

    @Test
    void testGetTodayMissionsWithProgress_WithNoProgress_CreatesEmpty() {
        UserDailyMissionResponse response = new UserDailyMissionResponse();
        response.setCurrentProgress(0);

        when(dailyMissionRepository.findByIsActiveTrue()).thenReturn(List.of(dummyMission));
        when(userDailyMissionRepository.findByUserIdAndDailyMissionIdAndDateAssigned(
                eq(dummyUserId), eq(dummyMission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(userDailyMissionMapper.toResponse(any(UserDailyMission.class))).thenReturn(response);

        List<UserDailyMissionResponse> result = dailyMissionService.getTodayMissionsWithProgress(dummyUserId);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getCurrentProgress());
    }

    @Test
    void testUpdateDailyMission_ShouldNotChangeIsActive_WhenIsActiveIsNull() {
        DailyMission existing = new DailyMission();
        existing.setId(UUID.randomUUID());
        existing.setName("Old");
        existing.setIsActive(true);

        DailyMission updatedData = new DailyMission();
        updatedData.setName("New");
        updatedData.setMilestone(2);
        updatedData.setIsActive(null);

        when(dailyMissionRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(dailyMissionRepository.save(any(DailyMission.class))).thenReturn(existing);

        DailyMission result = dailyMissionService.updateDailyMission(existing.getId(), updatedData);

        assertTrue(result.getIsActive());
    }

    @Test
    void testUpdateDailyMission_ShouldSetIsActive_WhenProvided() {
        DailyMission existing = new DailyMission();
        existing.setId(UUID.randomUUID());
        existing.setName("Old");
        existing.setIsActive(false);

        DailyMission updatedData = new DailyMission();
        updatedData.setName("New");
        updatedData.setMilestone(2);
        updatedData.setIsActive(true);

        when(dailyMissionRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(dailyMissionRepository.save(any(DailyMission.class))).thenReturn(existing);

        DailyMission result = dailyMissionService.updateDailyMission(existing.getId(), updatedData);

        assertTrue(result.getIsActive());
    }
}

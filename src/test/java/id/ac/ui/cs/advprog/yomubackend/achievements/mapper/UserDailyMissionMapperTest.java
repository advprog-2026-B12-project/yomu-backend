package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDailyMissionMapperTest {

    private UserDailyMissionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserDailyMissionMapper();
    }

    @Test
    void toResponse_mapsAllFields() {
        DailyMission mission = new DailyMission();
        mission.setId(UUID.randomUUID());
        mission.setName("Membaca 3 Artikel");

        UserDailyMission entity = new UserDailyMission();
        entity.setId(UUID.randomUUID());
        entity.setUserId(UUID.randomUUID());
        entity.setDailyMission(mission);
        entity.setDateAssigned(LocalDate.now());
        entity.setCurrentProgress(2);
        entity.setIsCompleted(false);
        entity.setCompletedAt(null);

        UserDailyMissionResponse result = mapper.toResponse(entity);

        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getUserId(), result.getUserId());
        assertEquals(mission.getId(), result.getMissionId());
        assertEquals("Membaca 3 Artikel", result.getMissionName());
        assertEquals(LocalDate.now(), result.getDateAssigned());
        assertEquals(2, result.getCurrentProgress());
        assertFalse(result.getIsCompleted());
        assertNull(result.getCompletedAt());
    }

    @Test
    void toResponse_mapsCompletedMission() {
        DailyMission mission = new DailyMission();
        mission.setId(UUID.randomUUID());
        mission.setName("Quick Read");

        LocalDateTime completedAt = LocalDateTime.now();

        UserDailyMission entity = new UserDailyMission();
        entity.setId(UUID.randomUUID());
        entity.setUserId(UUID.randomUUID());
        entity.setDailyMission(mission);
        entity.setDateAssigned(LocalDate.now());
        entity.setCurrentProgress(3);
        entity.setIsCompleted(true);
        entity.setCompletedAt(completedAt);

        UserDailyMissionResponse result = mapper.toResponse(entity);

        assertTrue(result.getIsCompleted());
        assertEquals(completedAt, result.getCompletedAt());
    }
}

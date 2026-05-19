package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DailyMissionMapperTest {

    private DailyMissionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DailyMissionMapper();
    }

    @Test
    void toEntity_mapsAllFields() {
        DailyMissionRequest request = new DailyMissionRequest();
        request.setName("Membaca 3 Artikel");
        request.setDescription("Selesaikan 3 bacaan hari ini");
        request.setMilestone(3);
        request.setEventType("READING_COMPLETED");
        request.setIsActive(true);

        DailyMission result = mapper.toEntity(request);

        assertEquals("Membaca 3 Artikel", result.getName());
        assertEquals("Selesaikan 3 bacaan hari ini", result.getDescription());
        assertEquals(3, result.getMilestone());
        assertEquals(AchievementEvent.READING_COMPLETED, result.getEventType());
        assertTrue(result.getIsActive());
    }

    @Test
    void toEntity_defaultsMilestoneToOne_whenNull() {
        DailyMissionRequest request = new DailyMissionRequest();
        request.setName("Test");
        request.setEventType("QUIZ_FINISHED");
        request.setMilestone(null);

        DailyMission result = mapper.toEntity(request);

        assertEquals(1, result.getMilestone());
    }

    @Test
    void toEntity_throwsException_whenEventTypeInvalid() {
        DailyMissionRequest request = new DailyMissionRequest();
        request.setName("Test");
        request.setEventType("NOT_VALID");

        assertThrows(ResponseStatusException.class, () -> mapper.toEntity(request));
    }

    @Test
    void toResponse_mapsAllFields() {
        DailyMission entity = new DailyMission();
        entity.setId(UUID.randomUUID());
        entity.setName("Membaca 3 Artikel");
        entity.setDescription("Selesaikan 3 bacaan hari ini");
        entity.setMilestone(3);
        entity.setEventType(AchievementEvent.READING_COMPLETED);
        entity.setIsActive(true);

        DailyMissionResponse result = mapper.toResponse(entity);

        assertEquals(entity.getId(), result.getId());
        assertEquals("Membaca 3 Artikel", result.getName());
        assertEquals("Selesaikan 3 bacaan hari ini", result.getDescription());
        assertEquals(3, result.getMilestone());
        assertEquals(AchievementEvent.READING_COMPLETED, result.getEventType());
        assertTrue(result.getIsActive());
    }
}

package id.ac.ui.cs.advprog.yomubackend.achievements.mapper;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AchievementMapperTest {

    private AchievementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AchievementMapper();
    }

    @Test
    void toEntity_mapsAllFields() {
        AchievementRequest request = new AchievementRequest();
        request.setName("Kutu Buku");
        request.setDescription("Selesaikan 10 bacaan");
        request.setIconUrl("https://example.com/icon.png");
        request.setPoints(100);
        request.setMilestone(10);
        request.setEventType("READING_COMPLETED");

        Achievement result = mapper.toEntity(request);

        assertEquals("Kutu Buku", result.getName());
        assertEquals("Selesaikan 10 bacaan", result.getDescription());
        assertEquals("https://example.com/icon.png", result.getIconUrl());
        assertEquals(100, result.getPoints());
        assertEquals(10, result.getMilestone());
        assertEquals(AchievementEvent.READING_COMPLETED, result.getEventType());
    }

    @Test
    void toEntity_defaultsPointsToZero_whenNull() {
        AchievementRequest request = new AchievementRequest();
        request.setName("Test");
        request.setEventType("QUIZ_FINISHED");
        request.setPoints(null);
        request.setMilestone(5);

        Achievement result = mapper.toEntity(request);

        assertEquals(0, result.getPoints());
    }

    @Test
    void toEntity_defaultsMilestoneToOne_whenNull() {
        AchievementRequest request = new AchievementRequest();
        request.setName("Test");
        request.setEventType("QUIZ_FINISHED");
        request.setMilestone(null);

        Achievement result = mapper.toEntity(request);

        assertEquals(1, result.getMilestone());
    }

    @Test
    void toEntity_throwsException_whenEventTypeInvalid() {
        AchievementRequest request = new AchievementRequest();
        request.setName("Test");
        request.setEventType("INVALID_EVENT");

        assertThrows(ResponseStatusException.class, () -> mapper.toEntity(request));
    }

    @Test
    void toResponse_mapsAllFields() {
        Achievement entity = new Achievement();
        entity.setId(UUID.randomUUID());
        entity.setName("Kutu Buku");
        entity.setDescription("Selesaikan 10 bacaan");
        entity.setIconUrl("https://example.com/icon.png");
        entity.setPoints(100);
        entity.setMilestone(10);
        entity.setEventType(AchievementEvent.READING_COMPLETED);

        AchievementResponse result = mapper.toResponse(entity);

        assertEquals(entity.getId(), result.getId());
        assertEquals("Kutu Buku", result.getName());
        assertEquals("Selesaikan 10 bacaan", result.getDescription());
        assertEquals("https://example.com/icon.png", result.getIconUrl());
        assertEquals(100, result.getPoints());
        assertEquals(10, result.getMilestone());
        assertEquals(AchievementEvent.READING_COMPLETED, result.getEventType());
    }
}

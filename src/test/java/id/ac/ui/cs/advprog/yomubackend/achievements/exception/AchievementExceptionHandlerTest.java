package id.ac.ui.cs.advprog.yomubackend.achievements.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AchievementExceptionHandlerTest {

    private AchievementExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AchievementExceptionHandler();
    }

    @Test
    void handleNotFound_withUserAchievementNotFoundException_returns404() {
        UUID id = UUID.randomUUID();
        UserAchievementNotFoundException ex = new UserAchievementNotFoundException(id);

        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("error").contains(id.toString()));
    }

    @Test
    void handleNotFound_withDailyMissionNotFoundException_returns404() {
        UUID id = UUID.randomUUID();
        DailyMissionNotFoundException ex = new DailyMissionNotFoundException(id);

        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("error").contains(id.toString()));
    }
}

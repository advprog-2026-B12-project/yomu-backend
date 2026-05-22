package id.ac.ui.cs.advprog.yomubackend.achievements.util;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class AchievementEventUtilsTest {

    @Test
    void validateAndNormalize_validEvent_returnsNormalized() {
        String result = AchievementEventUtils.validateAndNormalize("reading_completed");
        assertEquals(AchievementEvent.READING_COMPLETED, result);
    }

    @Test
    void validateAndNormalize_validEventAlreadyUppercase_returnsUnchanged() {
        String result = AchievementEventUtils.validateAndNormalize("QUIZ_FINISHED");
        assertEquals(AchievementEvent.QUIZ_FINISHED, result);
    }

    @Test
    void validateAndNormalize_invalidEvent_throwsBadRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> AchievementEventUtils.validateAndNormalize("UNKNOWN_EVENT"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void validateAndNormalize_nullEvent_throwsBadRequest() {
        assertThrows(ResponseStatusException.class,
                () -> AchievementEventUtils.validateAndNormalize(null));
    }
}

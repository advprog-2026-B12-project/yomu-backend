package id.ac.ui.cs.advprog.yomubackend.achievements.util;

import id.ac.ui.cs.advprog.yomubackend.achievements.constant.AchievementEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AchievementEventUtils {
    private AchievementEventUtils() {}

    public static String validateAndNormalize(String eventType) {
        String normalized = AchievementEvent.normalize(eventType);
        if (!AchievementEvent.isSupported(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid eventType. Supported values: " + AchievementEvent.supportedEvents()
            );
        }
        return normalized;
    }
}

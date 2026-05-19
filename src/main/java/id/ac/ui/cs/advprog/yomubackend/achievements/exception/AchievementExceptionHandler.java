package id.ac.ui.cs.advprog.yomubackend.achievements.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.yomubackend.achievements")
public class AchievementExceptionHandler {

    @ExceptionHandler({UserAchievementNotFoundException.class, DailyMissionNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }
}

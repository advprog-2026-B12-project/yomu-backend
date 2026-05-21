package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "id.ac.ui.cs.advprog.yomubackend.quiz")
public class QuizExceptionHandler {

    @ExceptionHandler({ReadingNotFoundException.class, QuestionNotFoundException.class, OptionNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(QuizAlreadyCompletedException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyCompleted(QuizAlreadyCompletedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler({ReadingNotOpenedException.class, QuizAlreadyStartedException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }
}

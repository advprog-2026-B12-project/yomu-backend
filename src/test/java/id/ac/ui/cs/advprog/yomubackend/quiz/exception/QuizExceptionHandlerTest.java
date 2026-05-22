package id.ac.ui.cs.advprog.yomubackend.quiz.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizExceptionHandlerTest {

    private QuizExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new QuizExceptionHandler();
    }

    @Test
    void handleNotFound_ReadingNotFoundException_Returns404() {
        ReadingNotFoundException ex = new ReadingNotFoundException(UUID.randomUUID());

        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody().get("error"));
    }

    @Test
    void handleAlreadyCompleted_Returns409() {
        QuizAlreadyCompletedException ex = new QuizAlreadyCompletedException();

        ResponseEntity<Map<String, String>> response = handler.handleAlreadyCompleted(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody().get("error"));
    }

    @Test
    void handleConflict_ReadingNotOpenedException_Returns409() {
        ReadingNotOpenedException ex = new ReadingNotOpenedException();

        ResponseEntity<Map<String, String>> response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody().get("error"));
    }
}

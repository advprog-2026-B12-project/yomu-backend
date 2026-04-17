package id.ac.ui.cs.advprog.yomubackend.quiz.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class QuizAttemptTest {

    @Test
    void testGettersAndSetters() {
        QuizAttempt attempt = new QuizAttempt();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID readingId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        attempt.setId(id);
        attempt.setUserId(userId);
        attempt.setReadingId(readingId);
        attempt.setScore(4);
        attempt.setTotal(5);
        attempt.setCreatedAt(now);

        assertEquals(id, attempt.getId());
        assertEquals(userId, attempt.getUserId());
        assertEquals(readingId, attempt.getReadingId());
        assertEquals(4, attempt.getScore());
        assertEquals(5, attempt.getTotal());
        assertEquals(now, attempt.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizAttempt a = new QuizAttempt();
        QuizAttempt b = new QuizAttempt();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        assertNotNull(new QuizAttempt().toString());
    }
}
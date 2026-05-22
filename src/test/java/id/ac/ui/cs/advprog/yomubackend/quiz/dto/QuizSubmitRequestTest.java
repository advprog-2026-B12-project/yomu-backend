package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class QuizSubmitRequestTest {

    @Test
    void testGettersAndSetters() {
        QuizSubmitRequest request = new QuizSubmitRequest();
        UUID readingId = UUID.randomUUID();
        Map<String, String> answers = Map.of("q1", "a1");

        request.setReadingId(readingId);
        request.setAnswers(answers);

        assertEquals(readingId, request.getReadingId());
        assertEquals(answers, request.getAnswers());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizSubmitRequest a = new QuizSubmitRequest();
        QuizSubmitRequest b = new QuizSubmitRequest();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        assertNotNull(new QuizSubmitRequest().toString());
    }
}
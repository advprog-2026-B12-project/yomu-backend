package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuizResultResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        QuizResultResponse response = new QuizResultResponse(4, 5);
        assertEquals(4, response.getScore());
        assertEquals(5, response.getTotal());
    }

    @Test
    void testSetters() {
        QuizResultResponse response = new QuizResultResponse(0, 0);
        response.setScore(3);
        response.setTotal(10);
        assertEquals(3, response.getScore());
        assertEquals(10, response.getTotal());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizResultResponse a = new QuizResultResponse(3, 5);
        QuizResultResponse b = new QuizResultResponse(3, 5);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        QuizResultResponse response = new QuizResultResponse(2, 4);
        assertNotNull(response.toString());
    }
}
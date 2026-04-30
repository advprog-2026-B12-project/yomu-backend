package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class QuizResponseTest {

    @Test
    void testGettersAndSetters() {
        QuizResponse response = new QuizResponse();
        UUID id = UUID.randomUUID();
        List<QuestionResponse> questions = List.of(
                QuestionResponse.builder()
                        .id(UUID.randomUUID())
                        .questionText("Sample question")
                        .build()
        );

        response.setId(id);
        response.setTitle("Title");
        response.setContent("Content");
        response.setQuestions(questions);

        assertEquals(id, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Content", response.getContent());
        assertEquals(questions, response.getQuestions());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizResponse a = new QuizResponse();
        QuizResponse b = new QuizResponse();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        QuizResponse response = new QuizResponse();
        assertNotNull(response.toString());
    }
}
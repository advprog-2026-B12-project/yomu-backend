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
        List<QuizQuestionResponse> questions = List.of(
                QuizQuestionResponse.builder()
                        .id(UUID.randomUUID())
                        .questionText("Sample question")
                        .options(List.of(QuizOptionResponse.builder()
                                .id(UUID.randomUUID())
                                .optionText("Sample option")
                                .build()))
                        .build()
        );

        response.setId(id);
        response.setTitle("Title");
        response.setCategory("Science");
        response.setQuestions(questions);

        assertEquals(id, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Science", response.getCategory());
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

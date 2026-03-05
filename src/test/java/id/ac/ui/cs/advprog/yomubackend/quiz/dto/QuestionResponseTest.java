package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionResponseTest {
    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();

        QuestionResponse response = QuestionResponse.builder()
                .id(id)
                .questionText("Question?")
                .build();

        assertEquals(id, response.getId());
        assertEquals("Question?", response.getQuestionText());
    }
}
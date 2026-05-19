package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizQuestionResponseTest {

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        QuizOptionResponse option = QuizOptionResponse.builder()
                .id(UUID.randomUUID())
                .optionText("Option")
                .build();

        QuizQuestionResponse response = QuizQuestionResponse.builder()
                .id(id)
                .questionText("Question?")
                .options(List.of(option))
                .build();

        assertEquals(id, response.getId());
        assertEquals("Question?", response.getQuestionText());
        assertEquals(List.of(option), response.getOptions());
    }
}

package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuizOptionResponseTest {

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();

        QuizOptionResponse response = QuizOptionResponse.builder()
                .id(id)
                .optionText("Option")
                .build();

        assertEquals(id, response.getId());
        assertEquals("Option", response.getOptionText());
    }
}

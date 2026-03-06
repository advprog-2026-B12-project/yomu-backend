package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionResponseTest {
    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();

        OptionResponse response = OptionResponse.builder()
                .id(id)
                .optionText("Option 1")
                .isCorrect(true)
                .build();

        assertEquals(id, response.getId());
        assertEquals("Option 1", response.getOptionText());
        assertTrue(response.isCorrect());
    }
}
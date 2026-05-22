package id.ac.ui.cs.advprog.yomubackend.quiz.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadingListItemResponseTest {

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();

        ReadingListItemResponse response = ReadingListItemResponse.builder()
                .id(id)
                .title("Reading")
                .category("News & Media")
                .completed(true)
                .build();

        assertEquals(id, response.getId());
        assertEquals("Reading", response.getTitle());
        assertEquals("News & Media", response.getCategory());
        assertTrue(response.isCompleted());
    }
}
